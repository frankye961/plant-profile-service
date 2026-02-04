package com.smart.watering.system.be.ai.model.records;

public record Threshold( double soilMoistureCriticalPct,
                         double soilMoistureLowPct,
                         double soilMoistureTargetMinPct,
                         double soilMoistureTargetMaxPct){}
