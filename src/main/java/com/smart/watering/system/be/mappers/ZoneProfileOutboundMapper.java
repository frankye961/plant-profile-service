package com.smart.watering.system.be.mappers;

import com.smart.watering.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ZoneProfileOutboundMapper {

    @Mappings({
            @Mapping(target = "type", constant = "ZONE_PROFILE_UPSERTED"),
            @Mapping(target = "version", constant = "1"),
            @Mapping(target = "messageId", source = "messageId"),
            @Mapping(target = "correlationId", source = "correlationId"),
            @Mapping(target = "ts", source = "ts"),
            @Mapping(target = "zoneId", source = "zone.zoneId"),
            @Mapping(target = "profileVersion", expression = "java(1L)"),
            @Mapping(target = "profile", source = "."),
            @Mapping(target = "gates.wateringAllowed", constant = "true"),
            @Mapping(target = "gates.blockedReason", constant = "OK"),
            @Mapping(target = "deviceState", ignore = true)
    })
    ZoneProfileUpsertedEvent toBootstrapProfile(IoTPlantEvent event);

    @Mappings({
            @Mapping(target = "zoneId", source = "zone.zoneId"),
            @Mapping(target = "zoneName", source = "zone.zoneName"),
            @Mapping(target = "linkedDeviceId", source = "device.deviceId"),
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "thresholds", ignore = true),
            @Mapping(target = "constraints", ignore = true),
            @Mapping(target = "calibrationPolicy", ignore = true),
            @Mapping(target = "updatedAt", source = "ts"),
            @Mapping(target = "source", constant = "BOOTSTRAP")
    })
    ZoneProfile toZoneProfile(IoTPlantEvent event);

    @AfterMapping
    default void applyDefaults(@MappingTarget ZoneProfile target, IoTPlantEvent source) {
        if (target.getThresholds() == null) {
            ZoneThresholds thresholds = new ZoneThresholds();
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
