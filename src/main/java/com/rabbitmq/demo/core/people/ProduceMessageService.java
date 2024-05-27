package com.rabbitmq.demo.core.people;

import org.springframework.stereotype.Service;

import com.rabbitmq.demo.infra.in.people.controller.PeopleController.PeopleCommand;
import com.rabbitmq.demo.infra.out.people.rabbitmq.ProduceMessage;

@Service
public class ProduceMessageService {
    private final ProduceMessage produce;
    
    public ProduceMessageService(final ProduceMessage produce) {
        this.produce = produce;
    }

    public void produce(final PeopleCommand command) {
        produce.send(command);
    }
}
