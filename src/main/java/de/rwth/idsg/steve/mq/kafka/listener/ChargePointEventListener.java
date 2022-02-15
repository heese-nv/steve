package de.rwth.idsg.steve.mq.kafka.listener;

import de.rwth.idsg.steve.mq.kafka.service.KafkaChargePointProducerService;
import de.rwth.idsg.steve.mq.message.ChargePointOperationRequest;
import de.rwth.idsg.steve.mq.message.HeartBeatEvent;
import de.rwth.idsg.steve.mq.message.StatusResponse;
import de.rwth.idsg.steve.service.ChargePointService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * @author ralf.heese
 */
@Slf4j
@Component
public class ChargePointEventListener {

    private final KafkaChargePointProducerService producerService;
    private final ChargePointService chargePointService;

    public ChargePointEventListener(KafkaChargePointProducerService producerService, ChargePointService chargePointService) {
        this.producerService = producerService;
        this.chargePointService = chargePointService;
    }

    @EventListener
    public void processClearCache(@NotNull ChargePointOperationRequest request) {
        log.debug("Clear cache at " + request.getMessageId());
        chargePointService.execute(request);
    }

    //
    // Events received from the charge point
    //

    @EventListener
    public void handleHeartBeatEvent(@NotNull HeartBeatEvent event) {
        log.debug("Received Heartbeat from {}", event.getChargePointId());

        producerService.send(event);
    }

    @EventListener
    public void handleStatusResponseEvent(@NotNull StatusResponse event) {
        log.debug("Received Status from {}", event.getChargePointId());

        producerService.send(event);
    }
}
