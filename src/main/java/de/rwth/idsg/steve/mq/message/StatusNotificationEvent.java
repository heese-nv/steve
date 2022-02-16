package de.rwth.idsg.steve.mq.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.joda.time.DateTime;

/**
 * @author ralf.heese
 */
@Getter
@Setter
@SuperBuilder
public class StatusNotificationEvent extends ChargePointOperationRequest {
    private String errorCode;
    private String errorInfo;

    private String status;

    private DateTime timestamp;

    private String vendorId;
    private String vendorErrorCode;
}
