package de.rwth.idsg.steve.api.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotBlank;
import java.util.Map;

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

    /** ID of a connector of the charge point */
    private Integer connectorId;

    /** Action name of operation */
    @NotBlank
    private String action;

    private Map<String, Object> params;

    @Nullable
    public String getStringParam(String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : null;
    }
}
