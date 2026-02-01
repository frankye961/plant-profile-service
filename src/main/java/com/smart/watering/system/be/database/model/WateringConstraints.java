package com.smart.watering.system.be.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WateringConstraints {
    private int cooldownSeconds;
    private int maxEventsPerDay;
    private QuietHours quietHours;
}
