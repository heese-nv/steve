package de.rwth.idsg.steve.api.contract;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author ralf.heese
 */
@Getter
@SuperBuilder
public class ChargePointOperationResponseContract {

    /** Globally unique request ID */
    @NotBlank
    private String messageId;

    /** ID of the charge point that the operation is performed on */
    @NotBlank
    private String chargePointId;

    /** Status of sending the operation */
    @NotBlank
    private String status;
}
