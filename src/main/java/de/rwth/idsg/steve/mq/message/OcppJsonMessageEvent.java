package de.rwth.idsg.steve.mq.message;

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
    private String chargePointId;

    private OcppJsonMessage message;
}
