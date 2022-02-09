package de.rwth.idsg.steve.mq.message;

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
public class StatusResponse extends OperationResponse {

    /** Status received from the charge point. See OCPP specification for the valid status values of a specific task. */
    private String status;
}
