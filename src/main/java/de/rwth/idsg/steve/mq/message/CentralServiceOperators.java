package de.rwth.idsg.steve.mq.message;

import de.rwth.idsg.steve.utils.ClassUtils;

import java.util.Collection;

/**
 * @author ralf.heese
 */
public class CentralServiceOperators {

    /** OCPP 1.6, section 5.4 */
    public static final String CHANGE_AVAILABILITY = "ChangeAvailability";

    /** OCPP 1.6, section 5.4 */
    public static final String CLEAR_CACHE = "ClearCache";

    private static final Collection<String> values;

    static {
        values = ClassUtils.getValuesOfStaticStrings(CentralServiceOperators.class);
    }

    private CentralServiceOperators() {
    }

    public static Collection<String> values() {
        return values;
    }
}
