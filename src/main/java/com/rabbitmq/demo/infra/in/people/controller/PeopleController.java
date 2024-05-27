package com.rabbitmq.demo.infra.in.people.controller;

import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.demo.core.people.ProduceMessageService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("people")
public class PeopleController {
    private final ProduceMessageService service;

    public PeopleController(final ProduceMessageService service) {
        this.service = service;
    }

    @PostMapping("send-data")
    ResponseEntity<Void> postMethodName(final @RequestBody PeopleCommand entity) {
        service.produce(entity);
        return ResponseEntity.noContent().build();
    }

    public record PeopleCommand(String name, Integer age) {
    }
}
