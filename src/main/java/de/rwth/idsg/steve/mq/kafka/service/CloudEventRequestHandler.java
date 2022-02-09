package de.rwth.idsg.steve.mq.kafka.service;

import de.rwth.idsg.steve.mq.kafka.service.mapper.CloudEventMessageMapper;
import de.rwth.idsg.steve.mq.message.ClearCacheMessage;
import de.rwth.idsg.steve.ocpp.task.StatusResponseCallback;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.service.callback.StatusEventCallback;
import io.cloudevents.CloudEvent;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static de.rwth.idsg.steve.utils.ValidationUtils.requireNotBlank;

/**
 * Process cloud events and publish its response as an application event
 *
 * @author ralf.heese
 */
@Component
@Slf4j
public class CloudEventRequestHandler implements CloudEventHandler {

    private final CloudEventMessageMapper messageMapper;
    private final ChargePointService chargePointService;
    private final ApplicationEventPublisher publisher;

    public CloudEventRequestHandler(ChargePointService chargePointService, ApplicationEventPublisher publisher, CloudEventMessageMapper messageMapper) {
        this.chargePointService = chargePointService;
        this.publisher = publisher;
        this.messageMapper = messageMapper;
    }

    @Override
    public void handleEvent(@NotNull CloudEvent event) {
        if (!isAccepted(event)) {
            return;
        }

        Object message = messageMapper.fromEvent(event);
        publisher.publishEvent(message);
    }

    @EventListener
    public void processClearCache(@NotNull ClearCacheMessage message) {
        log.debug("Clear cache at " + message.getRequestId());

        StatusResponseCallback callback = new StatusEventCallback(publisher, message.getRequestId());
        process(message.getChargePointId(), r -> chargePointService.clearCache(r, callback));
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

    /**
     * @param event
     *         cloud event
     * @return whether {@code event} can be processed by this handler
     */
    protected boolean isAccepted(@NotNull CloudEvent event) {
        String type = event.getType();
        return StringUtils.endsWith(type, ClearCacheMessage.class.getSimpleName());
    }
}
