package de.rwth.idsg.steve.mq.message;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.ws.data.OcppJsonMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Raw message exchanged between SteVe and charge points and vice versa.
 *
 * @author ralf.heese
 */
@Getter
@Setter
@Builder
public class OcppJsonMessageEvent {

    /** ID of the charge point related to this message (e.g., source or target) */
    private String chargePointId;

    /** Version of the message */
    private OcppVersion version;

    /** OCPP message */
    private OcppJsonMessage message;
}
