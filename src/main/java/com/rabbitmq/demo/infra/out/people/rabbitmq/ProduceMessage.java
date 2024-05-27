package com.rabbitmq.demo.infra.out.people.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rabbitmq.demo.infra.in.people.controller.PeopleController.PeopleCommand;

@Component
public class ProduceMessage {
    private final AmqpTemplate amqpTemplate;
    @Value("${spring.rabbitmq.people.queue}")
    private String queuePeople;
    @Value("${spring.rabbitmq.people.exchange}")
    private String exchangePeople;

    public ProduceMessage(final AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void send(final PeopleCommand command) {
        amqpTemplate.convertAndSend(exchangePeople, queuePeople, command);
    }
}
