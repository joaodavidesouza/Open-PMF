package com.openpmf.backend.event;

import com.openpmf.backend.model.SensorMeasurement;
import org.springframework.context.ApplicationEvent;

public class NewMeasurementEvent extends ApplicationEvent {
    
    private final SensorMeasurement measurement;

    public NewMeasurementEvent(Object source, SensorMeasurement measurement) {
        super(source);
        this.measurement = measurement;
    }

    public SensorMeasurement getMeasurement() {
        return measurement;
    }
}