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
     * @param clazz
     *         class
     * @return type of the message
     */
    @NotNull String getType(@NotNull Class<?> clazz);

    /**
     * @param type
     *         message type
     * @return Java class based on the message type
     */
    @NotNull Class<?> getClass(@NotNull String type) throws ClassNotFoundException;
}
