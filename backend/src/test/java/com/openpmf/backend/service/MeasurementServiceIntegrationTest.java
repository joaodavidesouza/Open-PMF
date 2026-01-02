package com.openpmf.backend.service;

import com.openpmf.backend.model.SensorMeasurement;
import com.openpmf.backend.repository.MeasurementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// This property explicitly disables the MQTT beans for this test, preventing the startup crash.
@SpringBootTest(properties = {"app.mqtt.enabled=false"})
@Testcontainers
class MeasurementServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Test
    void whenMeasurementIsProcessed_itShouldBeSavedInDatabase() {
        // Given
        SensorMeasurement measurement = new SensorMeasurement("test-machine-01", 1.23, Instant.now());

        // When
        measurementService.processAndSave(measurement);

        // Then
        Optional<SensorMeasurement> saved = measurementRepository.findById(1L);
        assertThat(saved).isPresent();
        assertThat(saved.get().getMachineId()).isEqualTo("test-machine-01");
    }
}