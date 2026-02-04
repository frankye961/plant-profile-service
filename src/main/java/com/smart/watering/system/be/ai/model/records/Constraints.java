package com.smart.watering.system.be.ai.model.records;

public record Constraints(
        int cooldownSeconds,
        int maxEventsPerDay,
        QuietHours quietHours
) {}
