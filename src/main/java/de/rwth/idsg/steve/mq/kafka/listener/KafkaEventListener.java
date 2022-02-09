package de.rwth.idsg.steve.mq.kafka.listener;

import de.rwth.idsg.steve.mq.kafka.service.KafkaChargePointProducerService;
import de.rwth.idsg.steve.mq.message.HeartBeatMessage;
import de.rwth.idsg.steve.mq.message.StatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * @author ralf.heese
 */
@Slf4j
@Component
public class KafkaEventListener {

    private final KafkaChargePointProducerService producerService;

    public KafkaEventListener(KafkaChargePointProducerService producerService) {
        this.producerService = producerService;
    }

    @EventListener
    public void handleHeartBeatEvent(@NotNull final HeartBeatMessage event) {
        log.debug("Received Heartbeat from {}", event.getChargePointId());

        producerService.send(event);
    }

    @EventListener
    public void handleStatusResponseEvent(@NotNull final StatusResponse event) {
        log.debug("Received Status from {}", event.getChargePointId());

        producerService.send(event);
    }
}
