package de.rwth.idsg.steve.mq.kafka.service;

import de.rwth.idsg.steve.mq.kafka.service.mapper.CloudEventMessageMapper;
import de.rwth.idsg.steve.mq.message.OcppJsonMessageEvent;
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
    public String send(@NotNull OperationResponse message) {
        return send(messageConverter.toEvent(message));
    }

    /**
     * Send the OCPP message to Kafka.
     *
     * @param message
     *         message
     * @return message ID
     */
    public String send(@NotNull OcppJsonMessageEvent message) {
        return send(topic + "-raw", messageConverter.toEvent(message));
    }

    public String send(@NotNull CloudEvent event) {
        return send(topic, event);
    }

    public String send(@NotNull String targetTopic, @NotNull CloudEvent event) {
        reactiveKafkaProducerTemplate.send(targetTopic, event)
                                     .doOnSuccess(senderResult -> log.info("SENT topic={}, offset={}: id={}, type={}, source={}, data={} ",
                                             targetTopic,
                                             senderResult.recordMetadata().offset(),
                                             event.getId(),
                                             event.getType(),
                                             event.getSource(),
                                             event.getData() == null ? "null" : new String(event.getData().toBytes())))
                                     .subscribe();

        return event.getId();
    }
}
