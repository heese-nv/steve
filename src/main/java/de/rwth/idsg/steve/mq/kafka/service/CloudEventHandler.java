package de.rwth.idsg.steve.mq.kafka.service;

import io.cloudevents.CloudEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author ralf.heese
 */
public interface CloudEventHandler {

    /**
     * Process the cloud event.
     *
     * @param event
     *         cloud event
     */
    void handleEvent(@NotNull CloudEvent event);
}
