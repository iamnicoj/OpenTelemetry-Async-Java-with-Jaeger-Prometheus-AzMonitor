package com.microsoft.cse.oteldemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMqListener {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMqListener.class);

    @Value("${spring.rabbitmq.queue.client}")
    private String listenerQueue;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Bean (name = "listenerConnectionFactory")
    @Primary
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        return cachingConnectionFactory;
    }


    @Bean
    SimpleMessageListenerContainer container(@Qualifier("listenerConnectionFactory") ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) throws InterruptedException {
        logger.info("Sleeping 30s SimpleMessageListenerContainer for Client");
        Thread.sleep(30000);
        logger.info("Creating SimpleMessageListenerContainer for Client");
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(listenerQueue);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RabbitMqReceiver receiver){
        logger.info("client is listening....");
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
