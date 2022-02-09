package de.rwth.idsg.steve.mq.kafka.service;

import com.github.f4b6a3.uuid.UuidCreator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author ralf.heese
 */
public class UuidMessageIdService implements MessageIdService {

    /** Unique node identifier */
    private final long nodeId;

    /**
     * @param nodeId
     *         value identifying uniquely the node.
     */
    public UuidMessageIdService(long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return time-ordered UUID with a node identifier
     */
    @Override
    public @NotNull UUID next() {
        return UuidCreator.getTimeOrdered(null, null, nodeId);
    }
}
