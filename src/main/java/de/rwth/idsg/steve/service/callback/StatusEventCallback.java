package de.rwth.idsg.steve.service.callback;

import de.rwth.idsg.steve.mq.message.StatusResponse;
import de.rwth.idsg.steve.ocpp.task.StatusResponseCallback;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Callback for charger point tasks which publishes the response as Spring event.
 *
 * @author ralf.heese
 */
@Getter
public class StatusEventCallback extends ChargePointApplicationEventCallback implements StatusResponseCallback {

    public StatusEventCallback(@NotNull ApplicationEventPublisher publisher, @NotNull String requestId) {
        super(publisher, requestId);
    }

    @Override
    public void success(String chargeBoxId, String status) {
        StatusResponse response = StatusResponse.builder()
                                                .requestId(getRequestId())
                                                .chargePointId(chargeBoxId)
                                                .status(status)
                                                .build();
        getPublisher().publishEvent(response);
    }
}
