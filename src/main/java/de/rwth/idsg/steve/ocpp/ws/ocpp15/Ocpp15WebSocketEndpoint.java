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
package de.rwth.idsg.steve.ocpp.ws.ocpp15;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.OcppProtocol;
import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.soap.CentralSystemService15_SoapServer;
import de.rwth.idsg.steve.ocpp.ws.AbstractWebSocketEndpoint;
import de.rwth.idsg.steve.ocpp.ws.FutureResponseContextStore;
import de.rwth.idsg.steve.ocpp.ws.pipeline.AbstractCallHandler;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Deserializer;
import de.rwth.idsg.steve.ocpp.ws.pipeline.IncomingPipeline;
import de.rwth.idsg.steve.ocpp.ws.pipeline.Serializer;
import ocpp.cs._2012._06.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2015
 */
@Component
public class Ocpp15WebSocketEndpoint extends AbstractWebSocketEndpoint {

    private final CentralSystemService15_SoapServer server;
    private final FutureResponseContextStore futureResponseContextStore;
    private final ApplicationEventPublisher publisher;

    public Ocpp15WebSocketEndpoint(CentralSystemService15_SoapServer server, FutureResponseContextStore futureResponseContextStore, ApplicationEventPublisher publisher) {
        this.server = server;
        this.futureResponseContextStore = futureResponseContextStore;
        this.publisher = publisher;
    }

    @PostConstruct
    public void init() {
        Deserializer deserializer = new Deserializer(futureResponseContextStore, Ocpp15TypeStore.INSTANCE, publisher);
        Serializer serializer = new Serializer(publisher);
        IncomingPipeline pipeline = new IncomingPipeline(serializer, deserializer, new Ocpp15CallHandler(server));
        super.init(pipeline);
    }

    @Override
    public OcppVersion getVersion() {
        return OcppVersion.V_15;
    }

    private static class Ocpp15CallHandler extends AbstractCallHandler {

        private final CentralSystemService15_SoapServer server;

        public Ocpp15CallHandler(CentralSystemService15_SoapServer server) {
            super();
            this.server = server;
        }

        @Override
        protected ResponseType dispatch(RequestType params, @NotNull String callContextJson) {
            ResponseType r;

            if (params instanceof BootNotificationRequest) {
                r = server.bootNotificationWithTransport((BootNotificationRequest) params, callContextJson, OcppProtocol.V_15_JSON);

            } else if (params instanceof FirmwareStatusNotificationRequest) {
                r = server.firmwareStatusNotification((FirmwareStatusNotificationRequest) params, callContextJson);

            } else if (params instanceof StatusNotificationRequest) {
                r = server.statusNotification((StatusNotificationRequest) params, callContextJson);

            } else if (params instanceof MeterValuesRequest) {
                r = server.meterValues((MeterValuesRequest) params, callContextJson);

            } else if (params instanceof DiagnosticsStatusNotificationRequest) {
                r = server.diagnosticsStatusNotification((DiagnosticsStatusNotificationRequest) params, callContextJson);

            } else if (params instanceof StartTransactionRequest) {
                r = server.startTransaction((StartTransactionRequest) params, callContextJson);

            } else if (params instanceof StopTransactionRequest) {
                r = server.stopTransaction((StopTransactionRequest) params, callContextJson);

            } else if (params instanceof HeartbeatRequest) {
                r = server.heartbeat((HeartbeatRequest) params, callContextJson);

            } else if (params instanceof AuthorizeRequest) {
                r = server.authorize((AuthorizeRequest) params, callContextJson);

            } else if (params instanceof DataTransferRequest) {
                r = server.dataTransfer((DataTransferRequest) params, callContextJson);
            } else {
                throw new IllegalArgumentException("Unexpected RequestType, dispatch method not found");
            }

            return r;
        }
    }
}
