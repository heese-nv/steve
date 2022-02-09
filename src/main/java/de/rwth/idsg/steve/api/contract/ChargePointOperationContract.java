package de.rwth.idsg.steve.api.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author ralf.heese
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargePointOperationContract {

    /** ID of the charge point that the operation is performed on */
    @NotBlank
    private String chargePointId;

    /** Type of operation */
    @NotBlank
    private String type;
}
