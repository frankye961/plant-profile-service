package com.smart.watering.system.be.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZoneThreshold {
    private float soilMoistureCriticalPct;
    private float soilMoistureLowPct;
    private float soilMoistureTargetMinPct;
    private float soilMoistureTargetMaxPct;

    private Float tempMinC;
    private Float tempMaxC;
    private Integer lightMinRaw;
}
