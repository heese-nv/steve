package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.ocpp.OcppCallback;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author ralf.heese
 */
public interface ChargePointServiceClient {

    /**
     * Execute a task using the default callback {@link CommunicationTask.DefaultOcppCallback}.
     *
     * @param task
     *         task to be executed
     * @param <S>
     *         Type of the parameters of the communication task
     * @param <RESPONSE>
     *         Type of response
     */
    default <S extends ChargePointSelection, RESPONSE> void executeTask(@NotNull CommunicationTask<S, RESPONSE> task) {
        executeTask(task, Collections.emptyList());
    }

    /**
     * Execute a task using the default callback {@link CommunicationTask.DefaultOcppCallback} and additional callbacks.
     *
     * @param task
     *         task to be executed
     * @param callbacks
     *         additional callbacks
     * @param <S>
     *         Type of the parameters of the communication task
     * @param <RESPONSE>
     *         Type of response
     */
    <S extends ChargePointSelection, RESPONSE> void executeTask(@NotNull CommunicationTask<S, RESPONSE> task, @Nullable List<OcppCallback<RESPONSE>> callbacks);
}
