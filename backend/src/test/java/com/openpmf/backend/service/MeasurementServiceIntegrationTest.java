package com.openpmf.backend.service;

import com.openpmf.backend.model.SensorMeasurement;
import com.openpmf.backend.repository.MeasurementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class MeasurementServiceIntegrationTest {

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Test
    void whenMeasurementIsProcessed_itShouldBeSavedInDatabase() {
        // Arrange
        SensorMeasurement measurement = new SensorMeasurement(
            "test-machine-01", 
            2.5, 
            Instant.now()
        );
        
        // Act
        SensorMeasurement saved = measurementService.processAndSave(measurement);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMachineId()).isEqualTo("test-machine-01");
        assertThat(saved.getVibration()).isEqualTo(2.5);
        
        // Verify it's actually in the database
        SensorMeasurement fromDb = measurementRepository.findById(saved.getId()).orElse(null);
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getMachineId()).isEqualTo("test-machine-01");
    }
}