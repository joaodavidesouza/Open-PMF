package com.openpmf.backend.service;

import com.openpmf.backend.model.SensorMeasurement;
import com.openpmf.backend.repository.MeasurementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class MeasurementServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    MeasurementService measurementService;

    @Autowired
    MeasurementRepository measurementRepository;

    @Test
    void whenMeasurementIsProcessed_itShouldBeSavedInDatabase() {
        // arrange
        SensorMeasurement measurement = new SensorMeasurement(
                "machine-1",
                42.0,
                Instant.now()
        );

        // act
        measurementService.processAndSave(measurement);

        // assert
        assertThat(measurementRepository.findAll()).hasSize(1);
    }
}
