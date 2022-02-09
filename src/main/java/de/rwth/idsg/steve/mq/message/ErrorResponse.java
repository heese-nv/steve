package de.rwth.idsg.steve.mq.message;

import de.rwth.idsg.steve.ocpp.ws.data.ErrorCode;
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
public class ErrorResponse extends OperationResponse {

    /** Error code provided by the charge point */
    private ErrorCode code;

    /** Error description provided by the charge point */
    private String description;

    /** Error details provided by the charge point */
    private String details;
}
