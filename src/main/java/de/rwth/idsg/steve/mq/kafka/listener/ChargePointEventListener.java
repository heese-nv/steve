package de.rwth.idsg.steve.mq.kafka.listener;

import de.rwth.idsg.steve.mq.kafka.service.KafkaChargePointProducerService;
import de.rwth.idsg.steve.mq.message.*;
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
    public void processOperationRequest(@NotNull ChargePointOperationRequest request) {
        String action = request.getAction();
        if (CentralServiceOperators.values().contains(action)) {
            log.debug("Central Service send request to {}", request.getMessageId());
            chargePointService.execute(request);
        } else if (ChargePointOperators.values().contains(action)) {
            log.debug("Charge point {} send request to Central Service", request.getMessageId());
            producerService.send(request);
        }
    }

    @EventListener
    public void handleStatusResponseEvent(@NotNull StatusResponse event) {
        log.debug("Received Status from {}", event.getChargePointId());
        producerService.send(event);
    }
}
