package com.smart.watering.system.be.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuietHours {
    private String start; // "HH:mm"
    private String end;   // "HH:mm"
    private String timezone; // optional
}
