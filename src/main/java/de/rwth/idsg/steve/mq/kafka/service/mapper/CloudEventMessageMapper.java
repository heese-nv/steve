package de.rwth.idsg.steve.mq.kafka.service.mapper;

import de.rwth.idsg.steve.mq.message.ChargePointMessage;
import io.cloudevents.CloudEvent;
import io.cloudevents.rw.CloudEventRWException;
import org.jetbrains.annotations.NotNull;

/**
 * @author ralf.heese
 */
public interface CloudEventMessageMapper {

    /**
     * Create a cloud event with a payload.
     *
     * @param data
     *         payload
     * @param <T>
     *         type of the payload
     * @return cloud event
     */
    @NotNull <T extends ChargePointMessage> CloudEvent toEvent(@NotNull T data) throws CloudEventRWException;

    /**
     * Extract an message instance from a cloud event. If the payload of the cloud event is {@code null} then
     * an unpopulated instance is returned.
     *
     * @param event
     *         cloud event
     * @param clazz
     *         expected class
     * @return message instance
     */
    @NotNull <T> T fromEvent(@NotNull CloudEvent event, @NotNull Class<T> clazz);

    /**
     * Extract an message instance from a cloud event. If the payload of the cloud event is {@code null} then
     * an unpopulated instance is returned.
     *
     * @param event
     *         cloud event
     * @return message instance
     */
    @NotNull Object fromEvent(@NotNull CloudEvent event);
}
