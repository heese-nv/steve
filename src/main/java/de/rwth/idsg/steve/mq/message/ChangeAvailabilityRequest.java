package de.rwth.idsg.steve.mq.message;

import de.rwth.idsg.steve.web.dto.ocpp.AvailabilityType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author ralf.heese
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ChangeAvailabilityRequest extends ChargePointOperationRequest {

    /**
     * Valid values are listed in the property {@link AvailabilityType#value()}
     * Not using the enum here because this package might get exported as separate jar.
     */
    private String availabilityType;
}
