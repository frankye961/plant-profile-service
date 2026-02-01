package com.smart.watering.system.be.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "zone-profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZoneProfiling {
    @Id
    private String zoneId;
    private String zoneName;
    private String deviceId;

    private boolean active;
    private long profileVersion;

    private ZoneThreshold thresholds;
    private WateringConstraints constraints;
    private CalibrationPolicy calibrationPolicy;
}
