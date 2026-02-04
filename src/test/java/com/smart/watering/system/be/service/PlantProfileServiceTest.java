package com.smart.watering.system.be.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smart.watering.model.IoTPlantEvent;
import com.smart.watering.model.ZoneProfileUpsertedEvent;
import com.smart.watering.system.be.ai.engine.AiEngine;
import com.smart.watering.system.be.database.model.ZoneProfiling;
import com.smart.watering.system.be.database.repositories.ZoneProfileRepository;
import com.smart.watering.system.be.mappers.ZoneMapper;
import com.smart.watering.system.be.mappers.ZoneProfileOutboundMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantProfileServiceTest {

    private static final String EVENT_JSON = """
            {
              "type":"IOT_PLANT_EVENT",
              "version":1,
              "messageId":"05fe5f23-700e-eaec-6314-cec56129d6c7",
              "ts":"1970-01-01T00:00:43.751Z",
              "device":{
                "deviceId":"device-123",
                "deviceName":"Balcony Arduino",
                "model":"Arduino UNO",
                "fw":"1.0.3",
                "batteryMv":4914,
                "rssi":-66
              },
              "zone":{
                "zoneId":"zone-1",
                "zoneName":"Basil",
                "sensor":{
                  "type":"MOCK",
                  "soilMoistureRaw":588,
                  "soilMoisturePct":52
                }
              }
            }
            """;

    @Mock
    private ZoneProfileRepository zoneProfileRepository;
    @Mock
    private ZoneProfileOutboundMapper outboundMapper;
    @Mock
    private ZoneMapper zoneMapper;
    @Mock
    private AiEngine aiEngine;

    private PlantProfileService plantProfileService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        plantProfileService = new PlantProfileService(zoneProfileRepository, outboundMapper, zoneMapper, aiEngine);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void elaborateZoneProfiling_createsZoneWhenMissing() throws Exception {
        IoTPlantEvent event = objectMapper.readValue(EVENT_JSON, IoTPlantEvent.class);
        ZoneProfiling zoneProfiling = new ZoneProfiling();
        ZoneProfileUpsertedEvent outbound = new ZoneProfileUpsertedEvent();

        when(zoneProfileRepository.findByZoneId("zone-1")).thenReturn(Mono.empty());
        when(zoneMapper.mapFromZoneToZoneProfiling(eq(event.getZone()))).thenReturn(zoneProfiling);
        when(zoneProfileRepository.save(zoneProfiling)).thenReturn(Mono.just(zoneProfiling));
        when(outboundMapper.toBootstrapProfile(event)).thenReturn(outbound);

        StepVerifier.create(plantProfileService.elaborateZoneProfiling(event))
                .expectNext(outbound)
                .verifyComplete();

        verify(zoneProfileRepository).save(zoneProfiling);
        verify(zoneMapper).mapFromZoneToZoneProfiling(eq(event.getZone()));
    }

    @Test
    void elaborateZoneProfiling_usesExistingProfileWhenPresent() throws Exception {
        IoTPlantEvent event = objectMapper.readValue(EVENT_JSON, IoTPlantEvent.class);
        ZoneProfiling existing = new ZoneProfiling();
        ZoneProfileUpsertedEvent outbound = new ZoneProfileUpsertedEvent();

        when(zoneProfileRepository.findByZoneId("zone-1")).thenReturn(Mono.just(existing));
        when(outboundMapper.toBootstrapProfile(event)).thenReturn(outbound);

        StepVerifier.create(plantProfileService.elaborateZoneProfiling(event))
                .expectNext(outbound)
                .verifyComplete();

        verify(zoneProfileRepository, never()).save(any());
    }
}
