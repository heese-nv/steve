package de.rwth.idsg.steve.web.dto.ocpp;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author ralf.heese
 */
@Getter
@RequiredArgsConstructor
public abstract class DelegateChargePointSelection<D extends ChargePointSelection> extends AbstractChargePointSelection {

    /** Delegate charge point selection */
    private final D delegate;

    @Override
    public @NotNull List<ChargePointSelect> getChargePointSelectList() {
        return delegate.getChargePointSelectList();
    }

    @Override
    public @NotNull String getMessageId() {
        return delegate.getMessageId();
    }

    @Override
    public void setMessageId(@NotNull String messageId) {
        if (delegate instanceof AbstractChargePointSelection) {
            ((AbstractChargePointSelection) delegate).setMessageId(messageId);
        } else {
            super.setMessageId(messageId);
        }
    }
}
