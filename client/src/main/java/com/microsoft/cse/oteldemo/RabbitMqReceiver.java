package com.microsoft.cse.oteldemo;

import entity.OrderMessage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.annotations.WithSpan;
import otelpoc.OtelOrderSpan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqReceiver {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqReceiver.class);

	@WithSpan(kind = SpanKind.CONSUMER)
    public void receiveMessage(String messageString) throws Exception{
        logger.info("Client received message From RabbitMQ: " + messageString);
        OrderMessage orderMessage = new OrderMessage();
        orderMessage = orderMessage.deserialize(messageString);

        OtelOrderSpan orderSpanBuilder = new OtelOrderSpan();

        Span orderSpan1 = orderSpanBuilder.GetOtelOrderSpan(orderMessage, "OrderFinalTrace", "otelclient");
        try (Scope scope = orderSpan1.makeCurrent()) {
            // Add any validation once message comes back to client after message workflow

        } finally {
            orderSpan1.end();
        }
    }
}
