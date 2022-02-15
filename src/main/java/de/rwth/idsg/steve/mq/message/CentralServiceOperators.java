package de.rwth.idsg.steve.mq.message;

import java.util.Collection;
import java.util.List;

/**
 * @author ralf.heese
 */
public class CentralServiceOperators {

    public static final String CLEAR_CACHE = "ClearCache";

    public static Collection<String> values() {
        return List.of(CLEAR_CACHE);
    }

    private CentralServiceOperators() {
    }
}
