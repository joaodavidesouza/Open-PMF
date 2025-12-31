package com.openpmf.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;


public record TelemetryData(
    @JsonProperty("machine_id") 
    String machineId,

    double vibration,
    Instant timestamp
) {}