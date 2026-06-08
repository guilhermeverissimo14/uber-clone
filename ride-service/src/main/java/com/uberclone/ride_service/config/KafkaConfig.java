package com.uberclone.ride_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.ride-requested}")
    private String rideRequestedTopic;

    @Value("${kafka.topics.ride-matched}")
    private String rideMatchedTopic;

    @Value("${kafka.topics.ride-events}")
    private String rideEventsTopic;

    @Bean
    public NewTopic rideRequestedTopic() {
        return TopicBuilder.name(rideRequestedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic rideMatchedTopic() {
        return TopicBuilder.name(rideMatchedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic rideEventsTopic() {
        return TopicBuilder.name(rideEventsTopic).partitions(3).replicas(1).build();
    }
}
