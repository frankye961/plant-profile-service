package com.smart.watering.system.be.rest;

import com.smart.watering.system.be.mappers.ZoneMapper;
import com.smart.watering.system.be.service.PlantProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Slf4j
@Service
public class TelemetryEventListener {

    private final ZoneMapper zoneMapper;
    private final PlantProfileService service;

    @Autowired
    public TelemetryEventListener(ZoneMapper zoneMapper, PlantProfileService service) {
        this.zoneMapper = zoneMapper;
        this.service = service;
    }

    @Bean
    public Function<Flux<Message<String>>, Message<?>> ingestTelemetryData(){
        
    }
}
