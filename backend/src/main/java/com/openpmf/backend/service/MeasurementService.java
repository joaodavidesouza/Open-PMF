package com.openpmf.backend.service;

import com.openpmf.backend.event.NewMeasurementEvent;
import com.openpmf.backend.model.SensorMeasurement;
import com.openpmf.backend.repository.MeasurementRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeasurementService {

    private final MeasurementRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public MeasurementService(MeasurementRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Processes and persists sensor data.
     * This layer is independent of the data source (MQTT, API, etc.)
     */
    @Transactional
    public void processAndSave(SensorMeasurement measurement) {
        // 1. Persist data into the database
        SensorMeasurement saved = repository.save(measurement);
        
        // 2. Publish internal event for the real-time SSE Dashboard
        eventPublisher.publishEvent(new NewMeasurementEvent(this, saved));
        
        System.out.println("🚀 Data Processed: Machine=" + saved.getMachineId() + " Vibration=" + saved.getVibration());
    }
}