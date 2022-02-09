package de.rwth.idsg.steve.mq.kafka.config;

import de.rwth.idsg.steve.config.KafkaConfiguration;
import de.rwth.idsg.steve.mq.kafka.service.MessageIdService;
import de.rwth.idsg.steve.mq.kafka.service.mapper.MessageTypeMapper;
import de.rwth.idsg.steve.mq.kafka.service.mapper.PackageMessageTypeMapper;
import de.rwth.idsg.steve.mq.kafka.service.UuidMessageIdService;
import io.cloudevents.CloudEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.net.URI;
import java.util.Random;

@Configuration
@Import(KafkaConfiguration.class)
public class ReactiveKafkaProducerConfig {

    @Bean
    public ReactiveKafkaProducerTemplate<String, CloudEvent> reactiveKafkaProducerTemplate(KafkaProperties properties) {
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(properties.buildProducerProperties()));
    }

    @Bean(name = "kafkaProducerId")
    public URI getClientId(@Value("spring.kafka.producer.node.id") String id) {
        return URI.create(String.format("urn:%s", id));
    }

    @Bean
    public MessageIdService getMessageIdGenerator(KafkaProperties properties) {
        long nodeId = Long.parseLong(properties.getProperties().getOrDefault("producer.node.id", "0"));
        if (nodeId == 0) {
            Random r = new Random();
            nodeId = r.nextLong();
        }

        return new UuidMessageIdService(nodeId);
    }

    @SuppressWarnings("unused")
    @Bean
    MessageTypeMapper getMessageTypeGenerator(KafkaProperties properties) {
        String namespace = properties.getProperties().getOrDefault("message.namespace", "de.rwth.idsg.steve.mq.message");

        return new PackageMessageTypeMapper(namespace);
    }
}
