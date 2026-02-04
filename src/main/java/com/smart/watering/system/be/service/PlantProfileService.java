package com.smart.watering.system.be.service;

import com.smart.watering.model.*;
import com.smart.watering.system.be.ai.engine.AiEngine;
import com.smart.watering.system.be.ai.model.records.ZoneSuggestion;
import com.smart.watering.system.be.database.model.ZoneProfiling;
import com.smart.watering.system.be.database.repositories.ZoneProfileRepository;
import com.smart.watering.system.be.mappers.ZoneMapper;
import com.smart.watering.system.be.mappers.ZoneProfileOutboundMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class PlantProfileService {

    private final ZoneProfileRepository zoneProfileRepository;
    private final ZoneProfileOutboundMapper mapper;
    private final ZoneMapper zoneMapper;
    private final AiEngine aiEngine;

    @Autowired
    public PlantProfileService(ZoneProfileRepository zoneProfileRepository, ZoneProfileOutboundMapper mapper,
                               ZoneMapper zoneMapper,
                               AiEngine aiEngine) {
        this.zoneProfileRepository = zoneProfileRepository;
        this.mapper = mapper;
        this.zoneMapper = zoneMapper;
        this.aiEngine = aiEngine;
    }

    public Mono<ZoneProfileUpsertedEvent> elaborateZoneProfiling(IoTPlantEvent event){
        String zoneId = event.getZone().getZoneId();
        Zone zone = event.getZone();

        return zoneProfileRepository.findByZoneId(zoneId)
                .switchIfEmpty(createZone(zone))
                .map(z -> mapOutbound(event));
    }

    private Mono<ZoneProfiling> createZone(Zone zone){
        ZoneProfiling zoneProfile = zoneMapper.mapFromZoneToZoneProfiling(zone);
        log.info("Zone to be saved {}", zoneProfile);
        return zoneProfileRepository.save(zoneProfile);
    }

    private ZoneThresholds enrichZoneThreshold(Zone zone){
        ZoneSuggestion suggestion = aiEngine.retrieveZoneSuggestion(zone);
        log.info("suggestion from ai: {}", suggestion);
        return null; //TODO enrich with suggestn from ai
    }

    private ZoneProfileUpsertedEvent mapOutbound(IoTPlantEvent event){
        return mapper.toBootstrapProfile(event);
    }
}
