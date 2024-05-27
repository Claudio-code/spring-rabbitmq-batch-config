package com.rabbitmq.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplateBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
class RabbitmqConfig {
    private static final String EXCHANGE = "x-dead-letter-exchange";
    private static final String ROUTING_KEY = "x-dead-letter-routing-key";
    @Value("${spring.rabbitmq.people.queue}")
    private String queuePeople;
    @Value("${spring.rabbitmq.people.exchange}")
    private String exchangePeople;
    @Value("${spring.rabbitmq.people.dlq.queue}")
    private String queuePeopleDlq;
    @Value("${spring.rabbitmq.people.dlq.exchange}")
    private String exchangePeopleDlq;
    @Value("${spring.rabbitmq.listener.simple.retry.initial-interval}")
    private Long initialInterval;
    @Value("${spring.rabbitmq.listener.simple.retry.max-interval}")
    private Long maxInterval;
    @Value("${spring.rabbitmq.listener.simple.retry.max-attempts}")
    private Integer maxAttempts;
    @Value("${spring.rabbitmq.listener.simple.retry.multiplier}")
    private Double multiplier;
    @Value("${spring.rabbitmq.listener.custom.receive-timeout}")
    private Long receiveTimeout;
    @Value("${spring.rabbitmq.listener.custom.batch-size}")
    private Integer batchSize;

    @Bean
    DirectExchange exchangePeopleDlq() {
        return new DirectExchange(exchangePeopleDlq);
    }

    @Bean
    DirectExchange exchangePeople() {
        return new DirectExchange(exchangePeople);
    }

    @Bean
    Queue queuePeopleDlq() {
        return QueueBuilder.durable(queuePeopleDlq)
                .build();
    }

    @Bean
    Queue queuePeople() {
        return QueueBuilder.durable(queuePeople)
                .withArgument(EXCHANGE, exchangePeopleDlq)
                .withArgument(ROUTING_KEY, queuePeopleDlq)
                .build();
    }

    @Bean
    Binding bindingQueuePeopleDlq() {
        return BindingBuilder.bind(queuePeopleDlq())
                .to(exchangePeopleDlq())
                .with(queuePeopleDlq);
    }

    @Bean
    Binding bindingQueuePeople() {
        return BindingBuilder.bind(queuePeople())
                .to(exchangePeople())
                .with(queuePeople);
    }

    @Bean
    MessageConverter converter() {
        final var mapper = new ObjectMapper().findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    SimpleRabbitListenerContainerFactory batchRabbitListenerContainerFactory(final ConnectionFactory connectionFactory,
            final MessageConverter converter) {
        final var factory = new SimpleRabbitListenerContainerFactory();
        final var retryTemplate = new RetryTemplateBuilder()
                .maxAttempts(maxAttempts)
                .exponentialBackoff(initialInterval, multiplier, maxInterval)
                .build();

        factory.setConnectionFactory(connectionFactory);
        factory.setBatchListener(true);
        factory.setRetryTemplate(retryTemplate);
        factory.setBatchSize(batchSize);
        factory.setReceiveTimeout(receiveTimeout);
        factory.setMessageConverter(converter);
        factory.setConsumerBatchEnabled(true);
        factory.setConsumerTagStrategy(queue -> queue);
        factory.setDefaultRequeueRejected(true);
        factory.setErrorHandler(new ConditionalRejectingErrorHandler());

        return factory;
    }
}
