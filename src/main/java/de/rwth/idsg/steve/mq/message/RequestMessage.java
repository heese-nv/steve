package de.rwth.idsg.steve.mq.message;

import org.jetbrains.annotations.NotNull;

/**
 * @author ralf.heese
 */
public interface RequestMessage {
    @NotNull
    String getRequestId();

    void setRequestId(@NotNull String requestId);
}
