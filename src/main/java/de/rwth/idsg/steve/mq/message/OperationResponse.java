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
public abstract class OperationResponse extends ChargePointMessage {

    /** Message ID provided by the charge point */
    private String messageId;
}

