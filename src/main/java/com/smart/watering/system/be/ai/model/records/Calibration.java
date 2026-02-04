package com.smart.watering.system.be.ai.model.records;

public record Calibration(
        boolean trustDevicePct,
        RawToPct rawToPct
) {}
