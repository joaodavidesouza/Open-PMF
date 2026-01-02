package com.openpmf.backend.service;

import com.openpmf.backend.event.NewMeasurementEvent;
import com.openpmf.backend.model.SensorMeasurement;
import com.openpmf.backend.repository.MeasurementRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MeasurementService(MeasurementRepository measurementRepository, 
                             ApplicationEventPublisher eventPublisher) {
        this.measurementRepository = measurementRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SensorMeasurement processAndSave(SensorMeasurement measurement) {
        SensorMeasurement saved = measurementRepository.save(measurement);
        eventPublisher.publishEvent(new NewMeasurementEvent(this, saved));
        return saved;
    }
}