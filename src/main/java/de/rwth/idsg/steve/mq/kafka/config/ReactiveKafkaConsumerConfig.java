package de.rwth.idsg.steve.mq.kafka.config;

import de.rwth.idsg.steve.config.KafkaConfiguration;
import io.cloudevents.CloudEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
@Import(KafkaConfiguration.class)
public class ReactiveKafkaConsumerConfig {

    @Bean
    public ReceiverOptions<String, CloudEvent> kafkaReceiverOptions(@Value(value = "${charger.consumer.topic}") String topic, KafkaProperties kafkaProperties) {
        ReceiverOptions<String, CloudEvent> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, CloudEvent> reactiveKafkaConsumerTemplate(ReceiverOptions<String, CloudEvent> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }
}
