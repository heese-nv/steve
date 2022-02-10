/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2021 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.*;
import de.rwth.idsg.steve.ocpp.task.*;
import de.rwth.idsg.steve.repository.TaskStore;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
@Slf4j
@Service
@Qualifier("ChargePointService12_Client")
public class ChargePointService12_Client implements ChargePointServiceClient {

    @Autowired
    protected ScheduledExecutorService executorService;
    @Autowired
    protected TaskStore taskStore;

    @Autowired
    private ChargePointService12_InvokerImpl invoker12;

    protected OcppVersion getVersion() {
        return OcppVersion.V_12;
    }

    protected ChargePointService12_Invoker getOcpp12Invoker() {
        return invoker12;
    }

    // -------------------------------------------------------------------------
    // Multiple Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    public int changeAvailability(ChangeAvailabilityParams params) {
        return executeTask(new ChangeAvailabilityTask(getVersion(), params));
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        return executeTask(new ChangeConfigurationTask(getVersion(), params));
    }

    public int clearCache(@NotNull MultipleChargePointSelect params) {
        return executeTask(new ClearCacheTask(getVersion(), params));
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        return executeTask(new GetDiagnosticsTask(getVersion(), params));
    }

    public int reset(ResetParams params) {
        return executeTask(new ResetTask(getVersion(), params));
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        return executeTask(new UpdateFirmwareTask(getVersion(), params));
    }

    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        return executeTask(new RemoteStartTransactionTask(getVersion(), params));
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        return executeTask(new RemoteStopTransactionTask(getVersion(), params));
    }

    public int unlockConnector(UnlockConnectorParams params) {
        return executeTask(new UnlockConnectorTask(getVersion(), params));
    }

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
    public <S extends ChargePointSelection, RESPONSE> int executeTask(@NotNull CommunicationTask<S, RESPONSE> task) {
        return executeTask(task, Collections.emptyList());
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
    public <S extends ChargePointSelection, RESPONSE> int executeTask(@NotNull CommunicationTask<S, RESPONSE> task, @Nullable List<OcppCallback<RESPONSE>> callbacks) {
        if (callbacks != null) {
            callbacks.forEach(task::addCallback);
        }

        Consumer<ChargePointSelect> consumer;
        if (task instanceof ChangeAvailabilityTask) {
            consumer = c -> getOcpp12Invoker().changeAvailability(c, (ChangeAvailabilityTask) task);

        } else if (task instanceof ChangeConfigurationTask) {
            consumer = c -> getOcpp12Invoker().changeConfiguration(c, (ChangeConfigurationTask) task);

        } else if (task instanceof ClearCacheTask) {
            consumer = c -> getOcpp12Invoker().clearCache(c, (ClearCacheTask) task);

        } else if (task instanceof GetDiagnosticsTask) {
            consumer = c -> getOcpp12Invoker().getDiagnostics(c, (GetDiagnosticsTask) task);

        } else if (task instanceof ResetTask) {
            consumer = c -> getOcpp12Invoker().reset(c, (ResetTask) task);

        } else if (task instanceof UpdateFirmwareTask) {
            consumer = c -> getOcpp12Invoker().updateFirmware(c, (UpdateFirmwareTask) task);

        } else if (task instanceof RemoteStartTransactionTask) {
            consumer = c -> getOcpp12Invoker().remoteStartTransaction(c, (RemoteStartTransactionTask) task);

        } else if (task instanceof RemoteStopTransactionTask) {
            consumer = c -> getOcpp12Invoker().remoteStopTransaction(c, (RemoteStopTransactionTask) task);

        } else if (task instanceof UnlockConnectorTask) {
            consumer = c -> getOcpp12Invoker().unlockConnector(c, (UnlockConnectorTask) task);

        } else {
            throw new SteveException("Unsupported operation: " + task.getOperationName());

        }

        return executeTask(task, consumer);
    }

    /**
     * Execute a task with the specified consumer.
     *
     * @param <S>
     *         Type of the parameters of the communication task
     * @param task
     *         task to be executed
     * @return internal task ID
     */
    protected <S extends ChargePointSelection, RESPONSE> int executeTask(@NotNull CommunicationTask<S, RESPONSE> task, @NotNull Consumer<ChargePointSelect> consumer) {
        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(consumer);
        return taskStore.add(task);
    }
}
