package com.microsoft.cse.oteldemo;

import entity.OrderMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.annotations.WithSpan;
import otelpoc.OtelOrderSpan;
import otelpoc.OtelConfiguration;

@ComponentScan(basePackageClasses = {OrderMessage.class, RabbitMqReceiver.class, RabbitMqListener.class})
@SpringBootApplication
public class SyntheticMessageSender implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(SyntheticMessageSender.class);

	private final RabbitTemplate rabbitTemplate;

	@Value("${spring.rabbitmq.queue.orders}")
	private String queue;

	@Value("${spring.rabbitmq.queue.orders.exchange}")
	private String topicExchangeName;

	@Value("${spring.rabbitmq.queue.orders.routing-key}")
	private String routingKey;

	public static void main(String[] args)
    {
		logger.info("client v2 is running...");
        SpringApplication.run(SyntheticMessageSender.class, args);
    }
	
    public SyntheticMessageSender(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		
	
		logger.info(System.getenv("AGENTTYPE"));

		if (System.getenv("AGENTTYPE").toLowerCase().equals("otel")){
			logger.info("Otel agent is enable, configuring the SDK...");
			OtelConfiguration.initOpenTelemetry();
		}
		else
			logger.info("App Insights agent is enable");
	}

	String[] customerIds = {"MK477", "NJ019", "SD007"};
	private OrderMessage getRandomizedOrderMessage(){
		OrderMessage om = new OrderMessage();
		Random r = new Random();
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.DOWN);
		DecimalFormat df2 = new DecimalFormat("#.###");
		df2.setRoundingMode(RoundingMode.DOWN);
		om.setOrderId(df2.format(r.nextInt(1000)));
		int customerIdIndex = r.nextInt(3);
		om.setCustomerId(customerIds[customerIdIndex]);
		om.setPrice(Double.parseDouble(df.format(r.nextDouble() * 1000)));
		om.setQuantity(r.nextInt(100));
		om.setStockNumber("CSE" + r.nextInt(10));
		om.setStatus("Initiated");
		return om;
	}

    public void run(String... arg0) throws Exception {
        
		// Send valid message
        logger.info("Sending valid message...");
        OrderMessage om = getRandomizedOrderMessage();
		sendMessage(om);
       
        logger.info("Sending second message...");
        OrderMessage om2 = getRandomizedOrderMessage();
        om2.setQuantity(0);
		sendMessage(om2);
        logger.info("Messages sent!");
    }

	@WithSpan(kind = SpanKind.PRODUCER)
	public void sendMessage(OrderMessage om) throws Exception{
        OtelOrderSpan orderSpanBuilder = new OtelOrderSpan();
		Span orderSpan1 = orderSpanBuilder.GetOtelOrderSpan(om, "OrderTrace", "otelclient");
        try (Scope scope = orderSpan1.makeCurrent()) {
            logger.info("Sending message : " + om.toString());
			rabbitTemplate.convertAndSend(topicExchangeName, routingKey, om.toString());
		} finally {
            orderSpan1.end();
        }
	}
}