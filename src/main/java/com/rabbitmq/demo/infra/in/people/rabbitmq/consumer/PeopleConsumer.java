package com.rabbitmq.demo.infra.in.people.rabbitmq.consumer;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.demo.infra.in.people.controller.PeopleController.PeopleCommand;

@Component
class PeopleConsumer {

    @RabbitListener(queues = "${spring.rabbitmq.people.queue}", containerFactory = "batchRabbitListenerContainerFactory")
    void consume(final List<PeopleCommand> commands) {
        for (final var peopleCommand : commands) {
            System.out.println(peopleCommand);
        }
    }
}
