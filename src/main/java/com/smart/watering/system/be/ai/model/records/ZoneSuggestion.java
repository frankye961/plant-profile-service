package com.smart.watering.system.be.ai.model.records;

public record ZoneSuggestion(Threshold threshold,
                             Constraints constraints,
                             Calibration calibration,
                             RawToPct raw,
                             QtHours qtHours) {}
