package worker1;

import entity.OrderMessageRepository;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.annotations.WithSpan;
import otelpoc.OtelOrderSpan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entity.OrderMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RabbitMqReceiver {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqReceiver.class);

    @Resource(name = "sendRabbitTemplate")
    private RabbitTemplate senderRabbitTemplate;

    @Autowired
    private OrderMessageRepository orderMessageRepository;

    @Value("${spring.rabbitmq.queue.fulfillment}")
    private String fulfillmentQueue;

    @Value("${spring.rabbitmq.queue.fulfillment.exchange}")
    private String fulfillmentExchange;

    @Value("${spring.rabbitmq.queue.fulfillment.routing-key}")
    private String fulfillmentRoutingKey;

    @Value("${spring.rabbitmq.queue.client}")
    private String clientQueue;

    @Value("${spring.rabbitmq.queue.client.exchange}")
    private String clientExchange;

    @Value("${spring.rabbitmq.queue.client.routing-key}")
    private String clientRoutingKey;

    @WithSpan(kind = SpanKind.CONSUMER)
    public void receiveMessage(String messageString) throws Exception {
        logger.info("Worker1 received message From RabbitMQ: " + messageString);
        OrderMessage orderMessage = new OrderMessage();
        orderMessage = orderMessage.deserialize(messageString);
        processMessage(orderMessage);
    }

    private void processMessage(OrderMessage orderMessage) throws Exception {

        OtelOrderSpan orderSpanBuilder = new OtelOrderSpan();

        Span orderSpan = orderSpanBuilder.GetOtelOrderSpan(orderMessage, "W1 Processing Order", "worker1");
        try (Scope scope = orderSpan.makeCurrent()) {
            logger.info("Validating Order Message");
            OrderMessage updatedMessage = updateStatus(orderMessage);

            logger.info("Saving updated message to Redis DB from Worker1");
            orderMessageRepository.save(updatedMessage);

            logger.info("Pushing to Queues");
            pushToQueue(updatedMessage);
        } finally {
            orderSpan.end();
        }
    }

    public OrderMessage updateStatus(OrderMessage orderMessage) {
        double quantPrice = orderMessage.getPrice() * orderMessage.getQuantity();
        // if quantity * price > 0, order is valid
        if (quantPrice > 0) {
            orderMessage.setStatus("VALID");
            logger.info("Updated OrderMessage Status = " + orderMessage.getStatus());
        } else {
            orderMessage.setStatus("INVALID");
            logger.error("Validation Failed. Updated OrderMessage Status = " + orderMessage.getStatus());
        }
        return orderMessage;
    }

    public void pushToQueue(OrderMessage orderMessage) throws InterruptedException {
        if (orderMessage.getStatus().equalsIgnoreCase("VALID")) {
            // If message if VALID, send to fulfilment queue
            sendMessageFulfillment(orderMessage);
        } else if (orderMessage.getStatus().equalsIgnoreCase("INVALID")) {
            // push to Client Queue - rejected message
            sendMessageClient(orderMessage);
        }

    }

    // Sending to queues logic
    @WithSpan(kind = SpanKind.PRODUCER)
    public void sendMessageClient(OrderMessage orderMessage) throws InterruptedException {
        OtelOrderSpan orderSpanBuilder = new OtelOrderSpan();

        Span orderSpan = orderSpanBuilder.GetOtelOrderSpan(orderMessage, "W1 Sending Invalid Order", "worker1");
        try (Scope scope = orderSpan.makeCurrent()) {
            logger.info("Sending updated message from Worker 1 to " + clientQueue);
            senderRabbitTemplate.convertAndSend(clientExchange, clientRoutingKey, orderMessage.toString());
            logger.info("Message sent from Worker1!");
        } finally {
            orderSpan.end();
        }
    }

    @WithSpan(kind = SpanKind.PRODUCER)
    public void sendMessageFulfillment(OrderMessage orderMessage) throws InterruptedException {
        OtelOrderSpan orderSpanBuilder = new OtelOrderSpan();

        Span orderSpan = orderSpanBuilder.GetOtelOrderSpan(orderMessage, "W1 Sending Valid Order", "worker1");
        try (Scope scope = orderSpan.makeCurrent()) {

            logger.info("Sending updated message from Worker 1 to " + fulfillmentQueue);
            senderRabbitTemplate.convertAndSend(fulfillmentExchange, fulfillmentRoutingKey, orderMessage.toString());
            logger.info("Message sent from Worker1!");
        } finally {
            orderSpan.end();
        }
    }

}