package com.smart.watering.system.be.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.watering.model.IoTPlantEvent;
import com.smart.watering.model.PlantProfileEvent;
import com.smart.watering.system.be.service.PlantProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class PlantProfileProcessor {

    private final PlantProfileService service;
    private final ObjectMapper mapper;

    @Autowired
    public PlantProfileProcessor(PlantProfileService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Bean
    public Function<Flux<Message<String>>, Flux<Message<?>>> ingestTelemetryData(){
        return inbound -> inbound
                .flatMap(this::processMessage);
    }

    private Mono<Message<PlantProfileEvent>> processMessage(Message<String> inbound){
        return mapDeviceMessage(inbound.getPayload())
    }

    private Message<PlantProfileEvent> buildMessage(PlantProfileEvent event) {
        return MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY, event.getDevice().getDeviceId())
                .build();
    }

    private Mono<IoTPlantEvent> mapDeviceMessage(String inbound) {
        return Mono.fromCallable(() -> mapper.readValue(inbound, IoTPlantEvent.class));
    }
}
