package com.openpmf.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "sensor_measurements")
@Data
@NoArgsConstructor
public class SensorMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String machineId;

    @Column(nullable = false)
    private Double vibration;

    @Column(nullable = false)
    private Instant timestamp;

    // Construtor utilitário para conversão rápida do Record
    public SensorMeasurement(String machineId, Double vibration, Instant timestamp) {
        this.machineId = machineId;
        this.vibration = vibration;
        this.timestamp = timestamp;
    }
}