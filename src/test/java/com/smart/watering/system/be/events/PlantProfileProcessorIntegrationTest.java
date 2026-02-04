package com.smart.watering.system.be.events;

import com.smart.watering.system.be.config.JacksonConfig;
import com.smart.watering.system.be.service.PlantProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {PlantProfileProcessor.class, JacksonConfig.class})
class PlantProfileProcessorIntegrationTest {

    @Autowired
    private PlantProfileProcessor processor;

    @MockBean
    private PlantProfileService plantProfileService;

    @MockBean
    private StreamBridge streamBridge;

    @Test
    void ingestTelemetryData_routesInvalidPayloadToDlq() {
        Message<String> inbound = MessageBuilder.withPayload("not-json")
                .setHeader(KafkaHeaders.RECEIVED_KEY, "zone-1")
                .build();

        Function<Flux<Message<String>>, Flux<Message<?>>> function = processor.ingestTelemetryData();

        StepVerifier.create(function.apply(Flux.just(inbound)))
                .verifyComplete();

        verify(streamBridge).send(eq("telemetryDlq-out-0"), argThat(message ->
                message.getHeaders().containsKey("dlq.errorType")
        ));
    }
}
