package de.rwth.idsg.steve.mq.kafka.listener;

import de.rwth.idsg.steve.mq.kafka.service.KafkaChargePointProducerService;
import de.rwth.idsg.steve.mq.message.ChangeAvailabilityRequest;
import de.rwth.idsg.steve.mq.message.ClearCacheRequest;
import de.rwth.idsg.steve.mq.message.HeartBeatEvent;
import de.rwth.idsg.steve.mq.message.StatusResponse;
import de.rwth.idsg.steve.ocpp.task.StatusResponseCallback;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.service.callback.StatusEventCallback;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static de.rwth.idsg.steve.utils.ValidationUtils.requireNotBlank;


/**
 * @author ralf.heese
 */
@Slf4j
@Component
public class ChargePointEventListener {

    private final KafkaChargePointProducerService producerService;
    private final ChargePointService chargePointService;
    private final ApplicationEventPublisher publisher;

    public ChargePointEventListener(KafkaChargePointProducerService producerService, ChargePointService chargePointService, ApplicationEventPublisher publisher) {
        this.producerService = producerService;
        this.chargePointService = chargePointService;
        this.publisher = publisher;
    }

    //
    // OCPP 1.2
    //

    @EventListener
    public void processClearCache(@NotNull ChangeAvailabilityRequest message) {
        log.debug("Clear cache at " + message.getRequestId());

        StatusResponseCallback callback = new StatusEventCallback(publisher, message.getRequestId());
        process(message.getChargePointId(), r -> chargePointService.clearCache(r, callback));
    }

    @EventListener
    public void processClearCache(@NotNull ClearCacheRequest message) {
        log.debug("Clear cache at " + message.getRequestId());

        StatusResponseCallback callback = new StatusEventCallback(publisher, message.getRequestId());
        process(message.getChargePointId(), r -> chargePointService.clearCache(r, callback));
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

    /**
     * Verify the existence of a charge point and apply the consumer.
     *
     * @param chargePointId
     *         charge point ID
     * @param consumer
     *         consumer
     */
    protected void process(@NotNull String chargePointId, @NotNull Consumer<ChargeBoxRecord> consumer) {
        requireNotBlank(chargePointId, "Non-blank charge point ID required");
        chargePointService.findChargeBoxById(chargePointId).ifPresent(consumer);
    }
}
