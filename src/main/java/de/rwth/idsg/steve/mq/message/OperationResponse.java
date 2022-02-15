package de.rwth.idsg.steve.mq.message;

import org.jetbrains.annotations.NotNull;

/**
 * Response to an operation initiated by an external system (e.g., the system managing charge points).
 *
 * @author ralf.heese
 */
public interface OperationResponse {

    /**
     * @return action linked to the response
     */
    @NotNull String getAction();

    /**
     * @param action
     *         action linked to the response
     */
    void setAction(@NotNull String action);

    /**
     * @return ID uniquely identifying the request
     */
    @NotNull String getMessageId();

    /**
     * @param messageId
     *         ID uniquely identifying the request
     */
    void setMessageId(@NotNull String messageId);
}

