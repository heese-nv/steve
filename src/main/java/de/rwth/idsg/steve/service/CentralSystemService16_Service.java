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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.idsg.steve.mq.message.ChargePointOperators;
import de.rwth.idsg.steve.mq.message.HeartbeatEvent;
import de.rwth.idsg.steve.mq.message.StatusNotificationEvent;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.ws.pipeline.CallContext;
import de.rwth.idsg.steve.repository.OcppServerRepository;
import de.rwth.idsg.steve.repository.SettingsRepository;
import de.rwth.idsg.steve.repository.dto.InsertConnectorStatusParams;
import de.rwth.idsg.steve.repository.dto.InsertTransactionParams;
import de.rwth.idsg.steve.repository.dto.UpdateChargeboxParams;
import de.rwth.idsg.steve.repository.dto.UpdateTransactionParams;
import jooq.steve.db.enums.TransactionStopEventActor;
import lombok.extern.slf4j.Slf4j;
import ocpp.cs._2015._10.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
@Slf4j
@Service
public class CentralSystemService16_Service {

    private final OcppServerRepository ocppServerRepository;
    private final SettingsRepository settingsRepository;

    private final OcppTagService ocppTagService;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper mapper;
    private ChargePointHelperService chargePointHelperService;

    public CentralSystemService16_Service(OcppServerRepository ocppServerRepository, SettingsRepository settingsRepository, OcppTagService ocppTagService,
                                          NotificationService notificationService, ApplicationEventPublisher applicationEventPublisher, ObjectMapper mapper) {
        this.ocppServerRepository = ocppServerRepository;
        this.settingsRepository = settingsRepository;
        this.ocppTagService = ocppTagService;
        this.notificationService = notificationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.mapper = mapper;
    }

    // There is a circular dependency between CentralSystemService16_Service and ChargePointHelperService. Autowiring via setting resolved this issue.
    @Autowired
    public void setChargePointHelperService(ChargePointHelperService chargePointHelperService) {
        this.chargePointHelperService = chargePointHelperService;
    }

    public BootNotificationResponse bootNotification(BootNotificationRequest parameters, String callContextJson, OcppProtocol ocppProtocol) {
        CallContext context = CallContext.fromJson(callContextJson);

        Optional<RegistrationStatus> status = chargePointHelperService.getRegistrationStatus(context.getChargeBoxId());
        notificationService.ocppStationBooted(context.getChargeBoxId(), status);
        DateTime now = DateTime.now();

        if (status.isEmpty()) {
            // Applies only to stations not in db (regardless of the registration_status field from db)
            log.error("The chargebox '{}' is NOT in database.", context.getChargeBoxId());
        } else {
            // Applies to all stations in db (even with registration_status Rejected)
            log.info("The boot of the chargebox '{}' with registration status '{}' is acknowledged.", context.getChargeBoxId(), status);
            UpdateChargeboxParams params =
                    UpdateChargeboxParams.builder()
                                         .ocppProtocol(ocppProtocol)
                                         .vendor(parameters.getChargePointVendor())
                                         .model(parameters.getChargePointModel())
                                         .pointSerial(parameters.getChargePointSerialNumber())
                                         .boxSerial(parameters.getChargeBoxSerialNumber())
                                         .fwVersion(parameters.getFirmwareVersion())
                                         .iccid(parameters.getIccid())
                                         .imsi(parameters.getImsi())
                                         .meterType(parameters.getMeterType())
                                         .meterSerial(parameters.getMeterSerialNumber())
                                         .chargeBoxId(context.getChargeBoxId())
                                         .heartbeatTimestamp(now)
                                         .build();

            ocppServerRepository.updateChargebox(params);
        }

        return new BootNotificationResponse()
                .withStatus(status.orElse(RegistrationStatus.REJECTED))
                .withCurrentTime(now)
                .withInterval(settingsRepository.getHeartbeatIntervalInSeconds());
    }

    public FirmwareStatusNotificationResponse firmwareStatusNotification(FirmwareStatusNotificationRequest parameters, String callContextJson) {
        CallContext context = CallContext.fromJson(callContextJson);

        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxFirmwareStatus(context.getChargeBoxId(), status);
        return new FirmwareStatusNotificationResponse();
    }

    public StatusNotificationResponse statusNotification(StatusNotificationRequest parameters, String callContextJson) {
        CallContext context = CallContext.fromJson(callContextJson);

        // Optional field
        DateTime timestamp = parameters.isSetTimestamp() ? parameters.getTimestamp() : DateTime.now();


        InsertConnectorStatusParams params =
                InsertConnectorStatusParams.builder()
                                           .chargeBoxId(context.getChargeBoxId())
                                           .connectorId(parameters.getConnectorId())
                                           .status(parameters.getStatus().value())
                                           .errorCode(parameters.getErrorCode().value())
                                           .timestamp(timestamp)
                                           .errorInfo(parameters.getInfo())
                                           .vendorId(parameters.getVendorId())
                                           .vendorErrorCode(parameters.getVendorErrorCode())
                                           .build();

        StatusNotificationEvent event = StatusNotificationEvent.builder()
                                                               .messageId(context.getMessageId())
                                                               .action(ChargePointOperators.STATUS_NOTIFICATION)
                                                               .chargePointId(params.getChargeBoxId())
                                                               .connectorId(params.getConnectorId())
                                                               .status(params.getStatus())
                                                               .errorCode(params.getErrorCode())
                                                               .timestamp(params.getTimestamp())
                                                               .errorInfo(params.getErrorInfo())
                                                               .vendorId(params.getVendorId())
                                                               .vendorErrorCode(params.getVendorErrorCode())
                                                               .build();
        applicationEventPublisher.publishEvent(event); // publish before DB update so that exceptions won't affect the event

        ocppServerRepository.insertConnectorStatus(params);

        if (parameters.getStatus() == ChargePointStatus.FAULTED) {
            notificationService.ocppStationStatusFailure(context.getChargeBoxId(), parameters.getConnectorId(), parameters.getErrorCode().value());
        }

        return new StatusNotificationResponse();
    }

    public MeterValuesResponse meterValues(MeterValuesRequest parameters, String callContextJson) {
        CallContext context = CallContext.fromJson(callContextJson);

        ocppServerRepository.insertMeterValues(
                context.getChargeBoxId(),
                parameters.getMeterValue(),
                parameters.getConnectorId(),
                parameters.getTransactionId()
        );

        return new MeterValuesResponse();
    }

    public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(DiagnosticsStatusNotificationRequest parameters, String callContextJson) {
        CallContext context = CallContext.fromJson(callContextJson);

        String status = parameters.getStatus().value();
        ocppServerRepository.updateChargeboxDiagnosticsStatus(context.getChargeBoxId(), status);
        return new DiagnosticsStatusNotificationResponse();
    }

    public StartTransactionResponse startTransaction(StartTransactionRequest parameters, String callContextJson) {
        CallContext context = CallContext.fromJson(callContextJson);

        // Get the authorization info of the user, before making tx changes (will affectAuthorizationStatus)
        IdTagInfo info = ocppTagService.getIdTagInfo(
                parameters.getIdTag(),
                true,
                () -> new IdTagInfo().withStatus(AuthorizationStatus.INVALID) // IdTagInfo is required
        );

        InsertTransactionParams params =
                InsertTransactionParams.builder()
                                       .chargeBoxId(context.getChargeBoxId())
                                       .connectorId(parameters.getConnectorId())
                                       .idTag(parameters.getIdTag())
                                       .startTimestamp(parameters.getTimestamp())
                                       .startMeterValue(Integer.toString(parameters.getMeterStart()))
                                       .reservationId(parameters.getReservationId())
                                       .eventTimestamp(DateTime.now())
                                       .build();

        int transactionId = ocppServerRepository.insertTransaction(params);

        notificationService.ocppTransactionStarted(transactionId, params);

        return new StartTransactionResponse()
                .withIdTagInfo(info)
                .withTransactionId(transactionId);
    }

    public StopTransactionResponse stopTransaction(StopTransactionRequest parameters, String callContextJson) {
        CallContext context = CallContext.fromJson(callContextJson);

        int transactionId = parameters.getTransactionId();
        String stopReason = parameters.isSetReason() ? parameters.getReason().value() : null;

        // Get the authorization info of the user, before making tx changes (will affectAuthorizationStatus)
        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(
                parameters.getIdTag(),
                false,
                () -> null
        );

        UpdateTransactionParams params =
                UpdateTransactionParams.builder()
                                       .chargeBoxId(context.getChargeBoxId())
                                       .transactionId(transactionId)
                                       .stopTimestamp(parameters.getTimestamp())
                                       .stopMeterValue(Integer.toString(parameters.getMeterStop()))
                                       .stopReason(stopReason)
                                       .eventTimestamp(DateTime.now())
                                       .eventActor(TransactionStopEventActor.station)
                                       .build();

        ocppServerRepository.updateTransaction(params);

        ocppServerRepository.insertMeterValues(context.getChargeBoxId(), parameters.getTransactionData(), transactionId);

        notificationService.ocppTransactionEnded(params);

        return new StopTransactionResponse().withIdTagInfo(idTagInfo);
    }

    public HeartbeatResponse heartbeat(HeartbeatRequest parameters, String callContextJson) {
        CallContext context = CallContext.fromJson(callContextJson);
        DateTime now = DateTime.now();

        HeartbeatEvent event = HeartbeatEvent.builder()
                                             .messageId(context.getMessageId())
                                             .action(ChargePointOperators.HEARTBEAT)
                                             .chargePointId(context.getChargeBoxId())
                                             .timestamp(now)
                                             .build();
        applicationEventPublisher.publishEvent(event); // publish before DB update so that exceptions won't affect the event

        ocppServerRepository.updateChargeboxHeartbeat(context.getChargeBoxId(), now);

        HeartbeatResponse heartbeatResponse = new HeartbeatResponse().withCurrentTime(now);
        try {
            String json_event = mapper.writeValueAsString(event);
            String json_result = mapper.writeValueAsString(heartbeatResponse);
            log.debug(json_event);
            log.debug(json_result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return heartbeatResponse;
    }

    public AuthorizeResponse authorize(AuthorizeRequest parameters, String callContextJson) {
        // Get the authorization info of the user
        IdTagInfo idTagInfo = ocppTagService.getIdTagInfo(
                parameters.getIdTag(),
                false,
                () -> new IdTagInfo().withStatus(AuthorizationStatus.INVALID)
        );

        return new AuthorizeResponse().withIdTagInfo(idTagInfo);
    }

    /**
     * Dummy implementation. This is new in OCPP 1.5. It must be vendor-specific.
     */
    public DataTransferResponse dataTransfer(DataTransferRequest parameters, String callContextJson) {
        CallContext context = CallContext.fromJson(callContextJson);

        log.info("[Data Transfer] Charge point: {}, Vendor Id: {}", context.getChargeBoxId(), parameters.getVendorId());
        if (parameters.isSetMessageId()) {
            log.info("[Data Transfer] Message Id: {}", parameters.getMessageId());
        }
        if (parameters.isSetData()) {
            log.info("[Data Transfer] Data: {}", parameters.getData());
        }

        // OCPP requires a status to be set. Since this is a dummy impl, set it to "Accepted".
        // https://github.com/RWTH-i5-IDSG/steve/pull/36
        return new DataTransferResponse().withStatus(DataTransferStatus.ACCEPTED);
    }
}
