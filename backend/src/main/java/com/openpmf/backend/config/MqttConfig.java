package com.openpmf.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpmf.backend.model.TelemetryData;
import com.openpmf.backend.model.SensorMeasurement;
import com.openpmf.backend.repository.MeasurementRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    private final ObjectMapper objectMapper;
    private final MeasurementRepository repository;

    public MqttConfig(ObjectMapper objectMapper, MeasurementRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        String brokerUrl = System.getenv("MQTT_BROKER_URL");
        if (brokerUrl == null || brokerUrl.isEmpty()) {
            brokerUrl = "tcp://localhost:1883";
        }

        String clientId = "open-pmf-backend-" + System.currentTimeMillis();
        
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(brokerUrl, clientId, "industry/textile/machine1");

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler hardwareDataHandler() {
        return message -> {
            String payload = (String) message.getPayload();
            
            System.out.println(">>> DEBUG: Raw Message Received: " + payload);

            try {
                TelemetryData telemetry = objectMapper.readValue(payload, TelemetryData.class);

                System.out.println(">>> DEBUG: Parsed Data - ID: " + telemetry.machineId() + ", Vib: " + telemetry.vibration());

                if (telemetry.machineId() == null) {
                    System.err.println(">>> CRITICAL: machineId is NULL! Check @JsonProperty mapping.");
                    return;
                }

                SensorMeasurement entity = new SensorMeasurement(
                    telemetry.machineId(),
                    telemetry.vibration(),
                    telemetry.timestamp()
                );
                
                repository.save(entity);
                System.out.println(">>> SUCCESS: Data saved to TimescaleDB (ID: " + entity.getId() + ")");

            } catch (Exception e) {
                System.err.println(">>> ERROR Processing Message: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}