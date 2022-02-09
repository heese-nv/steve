package de.rwth.idsg.steve.mq.kafka.service;

import de.rwth.idsg.steve.mq.kafka.service.mapper.CloudEventMessageMapper;
import de.rwth.idsg.steve.mq.kafka.service.mapper.MessageTypeMapper;
import de.rwth.idsg.steve.mq.message.ClearCacheRequest;
import de.rwth.idsg.steve.mq.message.OperationRequest;
import de.rwth.idsg.steve.ocpp.task.StatusResponseCallback;
import de.rwth.idsg.steve.service.ChargePointService;
import de.rwth.idsg.steve.service.callback.StatusEventCallback;
import de.rwth.idsg.steve.utils.ClassUtils;
import io.cloudevents.CloudEvent;
import jooq.steve.db.tables.records.ChargeBoxRecord;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.rwth.idsg.steve.utils.ValidationUtils.requireNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Process cloud events requesting an operation and publish its response as an application event
 *
 * @author ralf.heese
 */
@Component
@Slf4j
public class CloudEventRequestHandler implements CloudEventHandler {

    private static final String MESSAGE_PACKAGE_NAME = "de.rwth.idsg.steve.mq.message";

    private final Set<String> accepted;

    private final CloudEventMessageMapper messageMapper;
    private final MessageTypeMapper typeMapper;
    private final ChargePointService chargePointService;
    private final ApplicationEventPublisher publisher;

    public CloudEventRequestHandler(ChargePointService chargePointService, ApplicationEventPublisher publisher,
                                    CloudEventMessageMapper messageMapper, MessageTypeMapper typeMapper) {
        this.chargePointService = chargePointService;
        this.publisher = publisher;
        this.messageMapper = messageMapper;
        this.typeMapper = typeMapper;

        this.accepted = getAcceptedMessageTypes();
    }

    private Set<String> getAcceptedMessageTypes() {
        return ClassUtils.getClassesWithInterface(MESSAGE_PACKAGE_NAME, OperationRequest.class).values().stream()
                         .map(typeMapper::getType).collect(Collectors.toSet());
    }

    @Override
    public void handleEvent(@NotNull CloudEvent event) {
        if (accepts(event)) {
            Object message = messageMapper.fromEvent(event);
            publisher.publishEvent(message);
        }
    }

    @EventListener
    public void processClearCache(@NotNull ClearCacheRequest message) {
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
    @Override
    public boolean accepts(@NotNull CloudEvent event) {
        return isNotBlank(event.getType()) && accepted.contains(event.getType());
    }
}
