package de.rwth.idsg.steve.mq.message;

import de.rwth.idsg.steve.web.dto.ocpp.AvailabilityType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static de.rwth.idsg.steve.mq.message.CentralServiceOperators.CHANGE_AVAILABILITY;

/**
 * @author ralf.heese
 */
@SuppressWarnings("unused")
@Getter
@Setter
@SuperBuilder
public class ChangeAvailabilityRequest extends ChargePointOperationRequest {

    /**
     * Valid values are listed in the property {@link AvailabilityType#value()}
     * Not using the enum here because this package might get exported as separate jar.
     */
    private String availabilityType;

    public ChangeAvailabilityRequest() {
        super(CHANGE_AVAILABILITY);
    }
}
