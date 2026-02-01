package com.smart.watering.system.be.mappers;

import com.smart.watering.model.*;
import com.smart.watering.system.be.database.model.ZoneProfiling;
import org.mapstruct.*;
import org.springframework.lang.Nullable;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ZoneProfileOutboundMapper {

    @Mappings({
            @Mapping(target = "zoneId", source = "zone.zoneId"),
            @Mapping(target = "zoneName", source = "zone.zoneName"),
            @Mapping(target = "linkedDeviceId", source = "device.deviceId"),

            // Bootstrap fields (not present in event)
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "profileVersion", expression = "java(1L)"),

            // These will be filled in @AfterMapping if null
            @Mapping(target = "thresholds", ignore = true),
            @Mapping(target = "constraints", ignore = true),
            @Mapping(target = "calibrationPolicy", ignore = true)
    })
    ZoneProfile toBootstrapProfile(IoTPlantEvent event);

    /**
     * Bootstrap defaults (no user input) — conservative generic plant defaults.
     * You can later tune these automatically and bump profileVersion in service logic.
     */
    @AfterMapping
    default void applyDefaults(@MappingTarget ZoneProfile target, IoTPlantEvent source) {
        if (target.getThresholds() == null) {
            ZoneThresholds t = new ZoneThresholds();
            t.setSoilMoistureCriticalPct(30f);
            t.setSoilMoistureLowPct(40f);
            t.setSoilMoistureTargetMinPct(55f);
            t.setSoilMoistureTargetMaxPct(75f);
            // optional thresholds left null by default
            target.setThresholds(t);
        }

        if (target.getConstraints() == null) {
            WateringConstraints c = new WateringConstraints();
            c.setCooldownSeconds(6 * 60 * 60); // 6h
            c.setMaxEventsPerDay(2);

            QuietHours q = new QuietHours();
            q.setStart("22:00");
            q.setEnd("07:00");
            q.setTimezone("Europe/Warsaw"); // your project timezone
            c.setQuietHours(q);

            target.setConstraints(c);
        }

        if (target.getCalibrationPolicy() == null) {
            CalibrationPolicy cp = new CalibrationPolicy();

            // Because your bridge already provides soilMoisturePct, trust it by default.
            // If later you detect inconsistencies, your service can flip this to false.
            cp.setTrustDevicePct(true);

            // Optional: if device sometimes provides pct, sometimes not, you can decide here:
            // cp.setTrustDevicePct(hasPct(source) != null && hasPct(source));

            target.setCalibrationPolicy(cp);
        }
    }

    @Nullable
    default Boolean hasPct(IoTPlantEvent e) {
        if (e == null || e.getZone() == null || e.getZone().getSensor() == null) return null;
        return e.getZone().getSensor().getSoilMoisturePct() != null;
    }
}
