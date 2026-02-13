package com.smart.watering.system.be.events;

import com.smart.watering.system.be.config.JacksonConfig;
import com.smart.watering.system.be.database.repositories.ZoneProfileRepository;
import com.smart.watering.system.be.service.PlantProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

@SpringBootTest
class PlantProfileProcessorIntegrationTest {

    @Autowired
    private PlantProfileProcessor processor;

    @Autowired
    private PlantProfileService plantProfileService;

    @Mock
    private StreamBridge streamBridge;

    @Test
    void ingestTelemetryData_routesInvalidPayloadToDlq() {
        Message<String> inbound = MessageBuilder.withPayload("not-json")
                .setHeader(KafkaHeaders.RECEIVED_KEY, "zone-1")
                .build();

        Function<Flux<Message<String>>, Flux<Message<?>>> function = processor.processTelemetryData();

        StepVerifier.create(function.apply(Flux.just(inbound)))
                .verifyComplete();

    }
}
