package de.rwth.idsg.steve.mq.message;

import org.jetbrains.annotations.NotNull;

/**
 * Class describing an operation requested by an external system (e.g., system managing charge points).
 *
 * @author ralf.heese
 */
public interface OperationRequest {
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
