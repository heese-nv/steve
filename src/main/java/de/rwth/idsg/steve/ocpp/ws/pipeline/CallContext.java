package de.rwth.idsg.steve.ocpp.ws.pipeline;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.JsonObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;

/**
 * @author ralf.heese
 */
@Getter
@Builder
@Jacksonized
public class CallContext {
    private String chargeBoxId;
    private String messageId;

    @NotNull
    public String toJson() {
        try {
            return JsonObjectMapper.INSTANCE.getMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new SteveException("Unable to serialise call context");
        }
    }

    @NotNull
    public static CallContext fromJson(@NotNull String json) {
        try {
            return JsonObjectMapper.INSTANCE.getMapper().readValue(json, CallContext.class);
        } catch (JsonProcessingException e) {
            throw new SteveException("Unable to deserialize call context");
        }
    }
}
