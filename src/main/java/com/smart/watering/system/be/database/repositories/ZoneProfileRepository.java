package com.smart.watering.system.be.database.repositories;

import com.smart.watering.system.be.database.model.ZoneProfile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneProfileRepository extends ReactiveMongoRepository<ZoneProfile, String> {
}
