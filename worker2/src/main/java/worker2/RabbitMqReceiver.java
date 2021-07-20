package worker2;

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
public class RabbitMqReceiver{
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqReceiver.class);

    @Resource(name = "sendRabbitTemplate")
    private RabbitTemplate senderRabbitTemplate;

    @Value("${spring.rabbitmq.queue.client}")
    private String clientQueue;

    @Value("${spring.rabbitmq.queue.client.exchange}")
    private String clientTopicExchangeName;

    @Value("${spring.rabbitmq.queue.client.routing-key}")
    private String clientRoutingKey;


    @Autowired
    private OrderMessageRepository orderMessageRepository;

    @WithSpan(kind = SpanKind.CONSUMER)
    public void receiveMessage(String messageString) throws InterruptedException{
        logger.info("Worker2 received message From RabbitMQ: " + messageString);
        OrderMessage orderMessage = new OrderMessage();
        orderMessage = orderMessage.deserialize(messageString);
        processMessage(orderMessage);
    }

    private void processMessage(OrderMessage orderMessage) throws InterruptedException{

        OtelOrderSpan orderSpanBuilder = new OtelOrderSpan();

        Span orderSpan = orderSpanBuilder.GetOtelOrderSpan(orderMessage, "W2 Processing Order", "worker2");
        try (Scope scope = orderSpan.makeCurrent()) {
            if (orderMessage.getStatus().equalsIgnoreCase("VALID")){
                orderMessage.setStatus("FULFILLED");
                logger.info("Order Fulfilled. Updated message status = " + orderMessage.getStatus());
            }else{
                orderMessage.setStatus("UNFULFILLED");
                logger.error("Order NOT Fulfilled. Updated message status = " + orderMessage.getStatus());
            }

            logger.info("Saving updated message to Redis DB from Worker2");
            orderMessageRepository.save(orderMessage);

            // Send back to client queue to complete message workflow
            sendMessageByTopic(orderMessage.toString());
        } finally {
            orderSpan.end();
        }
    }

    public void sendMessageByTopic(String message) {
        logger.info("Sending updated message from Worker 2 to " + clientQueue);
        senderRabbitTemplate.convertAndSend(clientTopicExchangeName, clientRoutingKey, message);
        logger.info("Message sent from Worker2!");
    }

}