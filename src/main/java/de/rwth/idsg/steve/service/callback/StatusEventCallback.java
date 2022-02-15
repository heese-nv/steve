package de.rwth.idsg.steve.service.callback;

import de.rwth.idsg.steve.mq.message.OperationRequest;
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

    public StatusEventCallback(@NotNull ApplicationEventPublisher publisher, @NotNull OperationRequest request) {
        super(publisher, request);
    }

    @Override
    public void success(String chargePointId, String status) {
        StatusResponse response = StatusResponse.builder()
                                                .action(getRequest().getAction())
                                                .messageId(getRequest().getMessageId())
                                                .chargePointId(chargePointId)
                                                .status(status)
                                                .build();
        getPublisher().publishEvent(response);
    }
}
