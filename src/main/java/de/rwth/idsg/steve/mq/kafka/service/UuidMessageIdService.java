package de.rwth.idsg.steve.mq.kafka.service;

import com.github.f4b6a3.uuid.UuidCreator;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author ralf.heese
 */
public class UuidMessageIdService implements MessageIdService {

    /**
     * @return time-ordered UUID with a node identifier
     */
    @Override
    public @NotNull UUID next() {
        return UuidCreator.getTimeOrdered();
    }
}
