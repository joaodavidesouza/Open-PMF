package com.openpmf.backend.controller;

import com.openpmf.backend.event.NewMeasurementEvent;
import com.openpmf.backend.model.SensorMeasurement;
import com.openpmf.backend.repository.MeasurementRepository;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/measurements")
public class SensorController {
    
    private final MeasurementRepository repository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SensorController(MeasurementRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SensorMeasurement> getAllMeasurements() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMeasurements() {
        SseEmitter emitter = new SseEmitter(1800000L); 
        this.emitters.add(emitter);
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        return emitter;
    }

    @EventListener
    public void onNewMeasurement(NewMeasurementEvent event) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        this.emitters.forEach(emitter -> {
            try {
                emitter.send(event.getMeasurement());
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        this.emitters.removeAll(deadEmitters);
    }
}