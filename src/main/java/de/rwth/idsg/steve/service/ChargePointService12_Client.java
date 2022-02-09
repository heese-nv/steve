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
        ChangeAvailabilityTask task = new ChangeAvailabilityTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(((CommunicationTask<?, ?>) task).getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().changeAvailability(c, task));

        return taskStore.add(task);
    }

    public int changeConfiguration(ChangeConfigurationParams params) {
        ChangeConfigurationTask task = new ChangeConfigurationTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().changeConfiguration(c, task));

        return taskStore.add(task);
    }

    public int clearCache(@NotNull MultipleChargePointSelect params) {
        ClearCacheTask task = new ClearCacheTask(getVersion(), params);

        executeTask(task);
        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().clearCache(c, task));

        return taskStore.add(task);
    }

    public int getDiagnostics(GetDiagnosticsParams params) {
        GetDiagnosticsTask task = new GetDiagnosticsTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().getDiagnostics(c, task));

        return taskStore.add(task);
    }

    public int reset(ResetParams params) {
        ResetTask task = new ResetTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().reset(c, task));

        return taskStore.add(task);
    }

    public int updateFirmware(UpdateFirmwareParams params) {
        UpdateFirmwareTask task = new UpdateFirmwareTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().updateFirmware(c, task));

        return taskStore.add(task);
    }

    // -------------------------------------------------------------------------
    // Single Execution - since OCPP 1.2
    // -------------------------------------------------------------------------

    public int remoteStartTransaction(RemoteStartTransactionParams params) {
        RemoteStartTransactionTask task = new RemoteStartTransactionTask(getVersion(), params);

        executeTask(task);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().remoteStartTransaction(c, task));

        return taskStore.add(task);
    }

    public int remoteStopTransaction(RemoteStopTransactionParams params) {
        RemoteStopTransactionTask task = new RemoteStopTransactionTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().remoteStopTransaction(c, task));

        return taskStore.add(task);
    }

    public int unlockConnector(UnlockConnectorParams params) {
        UnlockConnectorTask task = new UnlockConnectorTask(getVersion(), params);

        BackgroundService.with(executorService)
                         .forFirst(task.getParams().getChargePointSelectList())
                         .execute(c -> getOcpp12Invoker().unlockConnector(c, task));

        return taskStore.add(task);
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
    public <S extends ChargePointSelection, RESPONSE> void executeTask(@NotNull CommunicationTask<S, RESPONSE> task) {
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
    public <S extends ChargePointSelection, RESPONSE> void executeTask(@NotNull CommunicationTask<S, RESPONSE> task, @Nullable List<OcppCallback<RESPONSE>> callbacks) {
        if (callbacks != null) {
            callbacks.forEach(task::addCallback);
        }

        Consumer<ChargePointSelect> consumer;
        if (task instanceof ClearCacheTask) {
            consumer = c -> getOcpp12Invoker().clearCache(c, (ClearCacheTask) task);
        } else {
            throw new SteveException("Unsupported operation: " + task.getOperationName());
        }

        executeTask(task, consumer);
    }

    /**
     * Execute a task with the specified consumer.
     *
     * @param task
     *         task to be executed
     * @param <S>
     *         Type of the parameters of the communication task
     * @param <RESPONSE>
     *         Type of response
     */
    protected <S extends ChargePointSelection, RESPONSE> void executeTask(@NotNull CommunicationTask<S, RESPONSE> task, @NotNull Consumer<ChargePointSelect> consumer) {
        BackgroundService.with(executorService)
                         .forEach(task.getParams().getChargePointSelectList())
                         .execute(consumer);
    }
}
