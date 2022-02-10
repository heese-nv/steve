package de.rwth.idsg.steve.mq.kafka.listener;

import de.rwth.idsg.steve.mq.kafka.service.KafkaChargePointProducerService;
import de.rwth.idsg.steve.mq.message.OcppJsonMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author ralf.heese
 */
@Slf4j
@Component
public class OcppJsonMessageListener {

    private final KafkaChargePointProducerService producerService;

    public OcppJsonMessageListener(KafkaChargePointProducerService producerService) {
        this.producerService = producerService;
    }

    @EventListener
    public void processOcppJsonMessage(OcppJsonMessageEvent message) {
        log.debug("OCPP message linked to charge point {}", message.getChargePointId());

        producerService.send(message);
    }
}
