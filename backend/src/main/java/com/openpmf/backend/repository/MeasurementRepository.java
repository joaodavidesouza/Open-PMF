package com.openpmf.backend.repository;

import com.openpmf.backend.model.SensorMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends JpaRepository<SensorMeasurement, Long> {
    // Aqui poderemos adicionar queries específicas do TimescaleDB no futuro
}