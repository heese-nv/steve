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

    /** Message ID to be used */
    private String messageId;

    /** ID of the charge point that the operation is performed on */
    @NotBlank
    private String chargePointId;

    /** Action name of operation */
    @NotBlank
    private String action;
}
