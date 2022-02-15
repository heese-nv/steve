package de.rwth.idsg.steve.mq.kafka.service;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author ralf.heese
 */
@Slf4j
@Component
public class KafkaChargePointConsumerService {

    private final ReactiveKafkaConsumerTemplate<String, CloudEvent> reactiveKafkaConsumerTemplate;
    private final CloudEventHandler eventHandler;

    public KafkaChargePointConsumerService(ReactiveKafkaConsumerTemplate<String, CloudEvent> reactiveKafkaConsumerTemplate,
                                           CloudEventHandler eventHandler) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
        this.eventHandler = eventHandler;
    }

    private Flux<CloudEvent> consumeCloudEvent() {
        return reactiveKafkaConsumerTemplate
                .receiveAutoAck()
                // .delayElements(Duration.ofSeconds(2L)) // BACKPRESSURE
                .doOnNext(consumerRecord -> {
                    CloudEvent event = consumerRecord.value();
                    if (eventHandler.accepts(event)) {
                        log.info("RECV from topic={}, offset={}: id={}, type={}, source={}, data={} ",
                                consumerRecord.topic(),
                                consumerRecord.offset(),
                                event.getId(),
                                event.getType(),
                                event.getSource(),
                                event.getData() == null ? "null" : new String(event.getData().toBytes()));
                    }
                })
                .map(ConsumerRecord::value)
                .filter(eventHandler::accepts)
                .doOnNext(eventHandler::handleEvent)
                .doOnError(throwable -> log.error("something bad happened while consuming : {}", throwable.getMessage()));
    }

    /**
     * Start listening for Kafka messages
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("Start Kafka Charge Box Consumer");
        consumeCloudEvent().subscribe();
    }

    @PreDestroy
    public void preDestroy() {
        // nothing to clean up so far
    }
}
