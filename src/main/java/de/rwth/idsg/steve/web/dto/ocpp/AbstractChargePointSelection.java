package de.rwth.idsg.steve.web.dto.ocpp;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author ralf.heese
 */
public abstract class AbstractChargePointSelection implements ChargePointSelection {

    private String messageId;

    public AbstractChargePointSelection() {
    }

    public AbstractChargePointSelection(@NotNull String messageId) {
        this.messageId = messageId;
    }

    @NotNull
    @Override
    public String getMessageId() {
        return StringUtils.isNotBlank(messageId) ? messageId : ChargePointSelection.super.getMessageId();
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
