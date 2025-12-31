package com.openpmf.backend.config;

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

    public MqttConfig() {
        System.out.println("DEBUG: MqttConfig Class loaded successfully");
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

@Bean
public MessageProducer inbound() {
    // Tenta pegar a URL do Docker, se não achar, usa o localhost (para rodar no seu Fedora)
    String brokerUrl = System.getenv("MQTT_BROKER_URL");
    if (brokerUrl == null) {
        brokerUrl = "tcp://localhost:1883";
    }
    
    String clientId = "open-pmf-backend-" + System.currentTimeMillis();
    MqttPahoMessageDrivenChannelAdapter adapter =
            new MqttPahoMessageDrivenChannelAdapter(brokerUrl, clientId, "industry/textile/machine1");
    
    // ... resto do código igual
    return adapter;
}

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            System.out.println("\n--- RECEIVED DATA FROM LOOM ---");
            System.out.println("Payload: " + message.getPayload());
            System.out.println("-----------------------------\n");
        };
    }
}