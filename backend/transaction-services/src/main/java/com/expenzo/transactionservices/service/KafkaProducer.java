package com.expenzo.transactionservices.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(@NonNull String topic, Object data) {
        kafkaTemplate.send(topic, data);
    }
}
