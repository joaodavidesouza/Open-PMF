package com.openpmf.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpmf.backend.model.SensorMeasurement;
import com.openpmf.backend.repository.MeasurementRepository;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.time.Instant;

@Configuration
public class MqttConfig {

    @Value("${app.mqtt.topic}")
    private String topic;

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    // 1. Injecting credentials from application.properties
    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setCleanSession(true);
        
        // 2. ACTIVATING AUTHENTICATION
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true); // Extra reliability
        
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("backendClient", mqttClientFactory(), topic);
        
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(MeasurementRepository repository) {
        return message -> {
            String payload = (String) message.getPayload();
            System.out.println("📥 Received MQTT Message: " + payload);

            try {
                ObjectMapper mapper = new ObjectMapper();
                var jsonNode = mapper.readTree(payload);
                
                String machineId = jsonNode.get("machineId").asText();
                double vibration = jsonNode.get("vibration").asDouble();
                Instant timestamp = jsonNode.has("timestamp") 
                        ? Instant.parse(jsonNode.get("timestamp").asText()) 
                        : Instant.now();

                SensorMeasurement measurement = new SensorMeasurement(machineId, vibration, timestamp);
                repository.save(measurement);
                System.out.println("✅ Data Saved to DB: ID=" + measurement.getId());

            } catch (Exception e) {
                System.err.println("❌ Error processing MQTT message: " + e.getMessage());
            }
        };
    }
}