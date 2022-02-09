package de.rwth.idsg.steve.mq.message;

import org.jetbrains.annotations.NotNull;

/**
 * Response to an operation initiated by an external system (e.g., the system managing charge points).
 *
 * @author ralf.heese
 */
public interface OperationResponse {
    /**
     * @return ID uniquely identifying the request
     */
    @NotNull String getRequestId();

    /**
     * @param requestId
     *         ID uniquely identifying the request
     */
    void setRequestId(@NotNull String requestId);
}
