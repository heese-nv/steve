package de.rwth.idsg.steve.mq.kafka.config;

import de.rwth.idsg.steve.config.KafkaConfiguration;
import de.rwth.idsg.steve.mq.kafka.service.MessageIdService;
import de.rwth.idsg.steve.mq.kafka.service.UuidMessageIdService;
import de.rwth.idsg.steve.mq.kafka.service.mapper.MessageTypeMapper;
import de.rwth.idsg.steve.mq.kafka.service.mapper.PackageMessageTypeMapper;
import io.cloudevents.CloudEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.net.URI;

import static de.rwth.idsg.steve.mq.kafka.config.CustomKafkaProperties.MESSAGE_NAMESPACE;
import static de.rwth.idsg.steve.mq.kafka.config.CustomKafkaProperties.PRODUCER_NODE_ID;

@Configuration
@Import(KafkaConfiguration.class)
public class ReactiveKafkaProducerConfig {

    private static final String DEFAULT_NODE_ID = "cDF595daK5YJTpTR7szD";
    private static final String DEFAULT_NAMESPACE = "de.rwth.idsg.steve.mq.message";

    @Bean
    public ReactiveKafkaProducerTemplate<String, CloudEvent> reactiveKafkaProducerTemplate(KafkaProperties properties) {
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(properties.buildProducerProperties()));
    }

    @Bean(name = "kafkaProducerId")
    public URI getClientId(KafkaProperties properties) {
        return URI.create(String.format("urn:%s", properties.buildConsumerProperties().getOrDefault(PRODUCER_NODE_ID, DEFAULT_NODE_ID)));
    }

    @SuppressWarnings("unused")
    @Bean
    public MessageIdService getMessageIdGenerator(KafkaProperties properties) {
        return new UuidMessageIdService();
    }

    @SuppressWarnings("unused")
    @Bean
    MessageTypeMapper getMessageTypeGenerator(KafkaProperties properties) {
        String namespace = properties.getProperties().getOrDefault(MESSAGE_NAMESPACE, DEFAULT_NAMESPACE);

        return new PackageMessageTypeMapper(namespace);
    }
}
