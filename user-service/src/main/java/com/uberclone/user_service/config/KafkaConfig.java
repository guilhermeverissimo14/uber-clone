package com.uberclone.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.user-events}")
    private String userEventsTopic;

    @Value("${kafka.topics.driver-events}")
    private String driverEventsTopic;

    /**
     * Cria tópico de eventos de usuário
     */
    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder
                .name(userEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Cria tópico de eventos de motorista
     */
    @Bean
    public NewTopic driverEventsTopic() {
        return TopicBuilder
                .name(driverEventsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
