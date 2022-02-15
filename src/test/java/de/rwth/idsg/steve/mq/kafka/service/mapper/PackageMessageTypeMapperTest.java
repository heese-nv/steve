package de.rwth.idsg.steve.mq.kafka.service.mapper;

import de.rwth.idsg.steve.mq.message.ChargePointOperationRequest;
import de.rwth.idsg.steve.mq.message.ChargePointOperationResponse;
import org.junit.Test;

import static de.rwth.idsg.steve.mq.message.CentralServiceOperators.CLEAR_CACHE;
import static org.junit.Assert.assertEquals;

/**
 * @author ralf.heese
 */
public class PackageMessageTypeMapperTest {

    private final String NAMESPACE = "some_prefix";

    PackageMessageTypeMapper target = new PackageMessageTypeMapper(NAMESPACE);

    @Test
    public void getTypeRequest() {
        ChargePointOperationRequest request = ChargePointOperationRequest.builder()
                                                                         .messageId("dummy")
                                                                         .chargePointId("dummy")
                                                                         .action(CLEAR_CACHE)
                                                                         .build();
        assertEquals(NAMESPACE + ".ClearCache.req", target.getType(request));
    }

    @Test
    public void getTypeResponse() {
        ChargePointOperationResponse response = ChargePointOperationResponse.builder()
                                                                            .messageId("dummy")
                                                                            .chargePointId("dummy")
                                                                            .action(CLEAR_CACHE)
                                                                            .build();
        assertEquals(NAMESPACE + ".ClearCache.conf", target.getType(response));
    }

    @Test
    public void getClassForType() throws ClassNotFoundException {
        assertEquals(ChargePointOperationRequest.class, target.getClassForType(NAMESPACE + ".ClearCache.req"));
        assertEquals(ChargePointOperationResponse.class, target.getClassForType(NAMESPACE + ".ClearCache.conf"));
    }
}
