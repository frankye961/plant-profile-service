package com.smart.watering.system.be.mappers;

import com.smart.watering.model.IoTPlantEvent;
import com.smart.watering.system.be.database.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ZoneMapper {

    @Mappings({
            @Mapping(target = "zoneId", source = "zone.zoneId"),
            @Mapping(target = "zoneName", source = "zone.zoneName"),
            @Mapping(target = "deviceId", source = "device.deviceId"),
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "profileVersion", expression = "java(1L)"),
            @Mapping(target = "thresholds", ignore = true),
            @Mapping(target = "constraints", ignore = true),
            @Mapping(target = "calibrationPolicy", ignore = true)
    })
    ZoneProfiling mapFromEventToZoneProfiling(IoTPlantEvent event);

    @AfterMapping
    default void applyDefaults(@MappingTarget ZoneProfiling target, IoTPlantEvent source) {
        if (target.getThresholds() == null) {
            ZoneThreshold thresholds = new ZoneThreshold();
            thresholds.setSoilMoistureCriticalPct(30f);
            thresholds.setSoilMoistureLowPct(40f);
            thresholds.setSoilMoistureTargetMinPct(55f);
            thresholds.setSoilMoistureTargetMaxPct(75f);
            target.setThresholds(thresholds);
        }

        if (target.getConstraints() == null) {
            WateringConstraints constraints = new WateringConstraints();
            constraints.setCooldownSeconds(6 * 60 * 60);
            constraints.setMaxEventsPerDay(2);

            QuietHours quietHours = new QuietHours();
            quietHours.setStart("22:00");
            quietHours.setEnd("07:00");
            quietHours.setTimezone("Europe/Warsaw");
            constraints.setQuietHours(quietHours);

            target.setConstraints(constraints);
        }

        if (target.getCalibrationPolicy() == null) {
            CalibrationPolicy calibrationPolicy = new CalibrationPolicy();
            calibrationPolicy.setTrustDevicePct(true);
            target.setCalibrationPolicy(calibrationPolicy);
        }
    }
}
