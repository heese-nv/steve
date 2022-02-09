package de.rwth.idsg.steve.mq.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Response to an operation initiated by an external system (e.g., the system managing charge points).
 *
 * @author ralf.heese
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractChargePointEvent implements ChargePointEvent {

    /** Message ID provided by the charge point */
    private String messageId;

    /** Unique ID of the charge point (aka charge box) */
    private String chargePointId;

    /** ID of the connector (supposed to be greater than 0) */
    private Integer connectorId;
}

