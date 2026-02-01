package com.smart.watering.system.be.service;

import com.smart.watering.model.IoTPlantEvent;
import com.smart.watering.model.Zone;
import com.smart.watering.model.ZoneProfile;
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

    @Autowired
    public PlantProfileService(ZoneProfileRepository zoneProfileRepository, ZoneProfileOutboundMapper mapper,
                               ZoneMapper zoneMapper) {
        this.zoneProfileRepository = zoneProfileRepository;
        this.mapper = mapper;
        this.zoneMapper = zoneMapper;
    }

    public Mono<ZoneProfile> elaborateZoneProfiling(IoTPlantEvent event){
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

    private ZoneProfile mapOutbound(IoTPlantEvent event){
        return mapper.toBootstrapProfile(event);
    }
}
