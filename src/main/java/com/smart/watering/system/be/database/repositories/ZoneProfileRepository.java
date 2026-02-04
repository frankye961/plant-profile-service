package com.smart.watering.system.be.database.repositories;

import com.smart.watering.system.be.database.model.ZoneProfiling;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ZoneProfileRepository extends ReactiveMongoRepository<ZoneProfiling, String> {
    Mono<ZoneProfiling> findByZoneId(String zoneId);
}
