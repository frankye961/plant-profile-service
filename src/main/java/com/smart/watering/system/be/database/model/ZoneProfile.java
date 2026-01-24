package com.smart.watering.system.be.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(value = "zone-profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZoneProfile {
    @Id
    private String zoneId;
    private String name;
    private String plantType;
    private String location;
    private String deviceId;
    private String soilType;
    private int units;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private int profileVersion;
}
