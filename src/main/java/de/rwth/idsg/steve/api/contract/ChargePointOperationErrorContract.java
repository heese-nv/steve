package de.rwth.idsg.steve.api.contract;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * @author ralf.heese
 */
@Getter
@SuperBuilder
public class ChargePointOperationErrorContract {

    /** ID of the charge point that the operation is performed on */
    @NotBlank
    private String chargePointId;

    /** Type of operation */
    @NotBlank
    private String status;
}
