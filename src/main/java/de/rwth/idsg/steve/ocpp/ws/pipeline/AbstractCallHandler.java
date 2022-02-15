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
package de.rwth.idsg.steve.ocpp.ws.pipeline;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.ws.ErrorFactory;
import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonCall;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public abstract class AbstractCallHandler implements Consumer<CommunicationContext> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void accept(CommunicationContext context) {
        OcppJsonCall call = (OcppJsonCall) context.getIncomingMessage();

        String messageId = call.getMessageId();
        OcppJsonMessage message;
        try {
            CallContext callContext = CallContext.builder()
                                                 .chargeBoxId(context.getChargeBoxId())
                                                 .messageId(messageId)
                                                 .build();

            // TODO Hack to get the message ID to the handler.
            ResponseType response = dispatch(call.getPayload(), callContext.toJson());
            OcppJsonResult result = new OcppJsonResult();
            result.setPayload(response);
            result.setMessageId(messageId);
            message = result;
        } catch (Exception e) {
            log.error("Exception occurred", e);
            message = ErrorFactory.payloadProcessingError(messageId, e.getMessage());
        }

        context.setOutgoingMessage(message);
    }

    protected abstract ResponseType dispatch(RequestType params, @NotNull String callContextJson);
}
