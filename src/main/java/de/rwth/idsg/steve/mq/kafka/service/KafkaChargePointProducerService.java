package de.rwth.idsg.steve.mq.kafka.service;

import de.rwth.idsg.steve.mq.kafka.service.mapper.CloudEventMessageMapper;
import de.rwth.idsg.steve.mq.message.ChargePointEvent;
import de.rwth.idsg.steve.mq.message.OperationRequest;
import de.rwth.idsg.steve.mq.message.OperationResponse;
import io.cloudevents.CloudEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ralf.heese
 */
@Service
public class KafkaChargePointProducerService {
    private final Logger log = LoggerFactory.getLogger(KafkaChargePointProducerService.class);
    private final ReactiveKafkaProducerTemplate<String, CloudEvent> reactiveKafkaProducerTemplate;
    private final CloudEventMessageMapper messageConverter;

    @Value(value = "${charger.producer.topic}")
    private String topic;

    @Autowired
    public KafkaChargePointProducerService(ReactiveKafkaProducerTemplate<String, CloudEvent> reactiveKafkaProducerTemplate,
                                           CloudEventMessageMapper messageConverter) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
        this.messageConverter = messageConverter;
    }

    /**
     * Send the message to Kafka.
     *
     * @param message
     *         message
     * @return message ID
     */
    public String send(@NotNull OperationRequest message) {
        return send(messageConverter.toEvent(message));
    }

    /**
     * Send the message to Kafka.
     *
     * @param message
     *         message
     * @return message ID
     */
    public String send(@NotNull ChargePointEvent message) {
        return send(messageConverter.toEvent(message));
    }

    /**
     * Send the message to Kafka.
     *
     * @param message
     *         message
     * @return message ID
     */
    public String send(@NotNull OperationResponse message) {
        return send(messageConverter.toEvent(message));
    }

    public String send(@NotNull CloudEvent event) {
        log.info("send to topic={}, {}={},", topic, event.getType(), event);

        reactiveKafkaProducerTemplate.send(topic, event)
                                     .doOnSuccess(senderResult -> log.info("sent {} offset : {}", event, senderResult.recordMetadata().offset()))
                                     .subscribe();

        return event.getId();
    }
}
