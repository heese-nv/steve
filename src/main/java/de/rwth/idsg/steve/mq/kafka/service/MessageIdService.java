package de.rwth.idsg.steve.mq.kafka.service;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author ralf.heese
 */
public interface MessageIdService {

    /**
     * @return next generated message ID
     */
    @NotNull UUID next();
}
