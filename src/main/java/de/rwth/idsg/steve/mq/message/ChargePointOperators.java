package de.rwth.idsg.steve.mq.message;

import de.rwth.idsg.steve.utils.ClassUtils;

import java.util.Collection;

/**
 * @author ralf.heese
 */
public class ChargePointOperators {

    /** OCPP 1.6, section 4.6 */
    public static final String HEARTBEAT = "Heartbeat";

    /** OCPP 1.6, section 4.9 */
    public static final String STATUS_NOTIFICATION = "StatusNotification";

    private static final Collection<String> values;

    static {
        values = ClassUtils.getValuesOfStaticStrings(ChargePointOperators.class);
    }

    private ChargePointOperators() {
    }

    public static Collection<String> values() {
        return values;
    }
}
