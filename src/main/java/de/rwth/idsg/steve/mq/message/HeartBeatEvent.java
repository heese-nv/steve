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
@NoArgsConstructor
public class HeartBeatEvent extends ChargePointOperationRequest {

    private DateTime timestamp;
}
