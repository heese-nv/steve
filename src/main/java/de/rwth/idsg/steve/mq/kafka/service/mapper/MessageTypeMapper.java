package de.rwth.idsg.steve.mq.kafka.service.mapper;

import org.jetbrains.annotations.NotNull;

/**
 * @author ralf.heese
 */
public interface MessageTypeMapper {

    /**
     * @param o
     *         an object
     * @return type of the message
     */
    @NotNull String getType(@NotNull Object o);

    /**
     * @param action
     *         operation
     * @return request type of operation
     */
    @NotNull String getRequestType(@NotNull String action);

    /**
     * @param action
     *         operation
     * @return response type of operation
     */
    @NotNull String getResponseType(@NotNull String action);

    /**
     * @param type
     *         message type
     * @return Java class based on the message type
     */
    @NotNull Class<?> getClassForType(@NotNull String type) throws ClassNotFoundException;
}
