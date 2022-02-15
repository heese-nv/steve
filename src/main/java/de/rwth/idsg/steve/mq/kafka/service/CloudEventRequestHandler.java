package de.rwth.idsg.steve.mq.kafka.service;

import de.rwth.idsg.steve.mq.kafka.service.mapper.CloudEventMessageMapper;
import de.rwth.idsg.steve.mq.kafka.service.mapper.MessageTypeMapper;
import de.rwth.idsg.steve.mq.message.CentralServiceOperators;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Process cloud events requesting an operation and publish its response as an application event
 *
 * @author ralf.heese
 */
@Component
@Slf4j
public class CloudEventRequestHandler implements CloudEventHandler {

    private final Set<String> accepted;
    private final CloudEventMessageMapper messageMapper;
    private final MessageTypeMapper typeMapper;
    private final ApplicationEventPublisher publisher;

    public CloudEventRequestHandler(ApplicationEventPublisher publisher, CloudEventMessageMapper messageMapper, MessageTypeMapper typeMapper) {
        this.publisher = publisher;
        this.messageMapper = messageMapper;
        this.typeMapper = typeMapper;

        this.accepted = getAcceptedMessageTypes();
    }

    private Set<String> getAcceptedMessageTypes() {
        return CentralServiceOperators.values().stream()
                                      .map(typeMapper::getRequestType)
                                      .collect(Collectors.toSet());
    }

    @Override
    public void handleEvent(@NotNull CloudEvent event) {
        if (accepts(event)) {
            Object message = messageMapper.fromEvent(event);
            publisher.publishEvent(message);
        }
    }

    /**
     * @param event
     *         cloud event
     * @return whether {@code event} can be processed by this handler
     */
    @Override
    public boolean accepts(@NotNull CloudEvent event) {
        return accepted.contains(event.getType());
    }
}
