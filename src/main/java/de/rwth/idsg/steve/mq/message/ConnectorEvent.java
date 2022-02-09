package de.rwth.idsg.steve.mq.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author ralf.heese
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class ConnectorEvent extends AbstractOperationRequest {

    /** ID of the connector (supposed to be greater than 0) */
    private Integer connectorId;
}
