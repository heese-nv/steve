package de.rwth.idsg.steve.mq.kafka.service.mapper;

import de.rwth.idsg.steve.mq.message.ChargePointOperationRequest;
import de.rwth.idsg.steve.mq.message.ChargePointOperationResponse;
import de.rwth.idsg.steve.mq.message.OperationRequest;
import de.rwth.idsg.steve.mq.message.OperationResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.removeStart;

/**
 * Generate message types by replacing the name of the Java package with a namespace.
 *
 * @author ralf.heese
 */
public class PackageMessageTypeMapper implements MessageTypeMapper {

    private static final String SUFFIX_REQUEST = ".req";
    private static final String SUFFIX_RESPONSE = ".conf";

    /** Name of the package containing the message classes */
    private static final String MESSAGE_PACKAGE = "de.rwth.idsg.steve.mq.message";

    private final String namespace;

    public PackageMessageTypeMapper(@NotNull String namespace) {
        this.namespace = namespace;
    }

    /**
     * Get the message type for a class. It maps the suffixes {@code Request} and {@code Response}
     * to {@code .ref} and {@code .conf}, respectively. For example, {@code StartTransactionRequest}
     * is mapped to {@code StartTransaction.req}.
     *
     * @param o
     *         an object
     * @return message type
     */
    @Override
    public @NotNull String getType(@NotNull Object o) {
        if (o instanceof OperationRequest) {
            return getRequestType(((OperationRequest) o).getAction());
        } else if (o instanceof OperationResponse) {
            return getResponseType(((OperationResponse) o).getAction());
        } else {
            return getType(o.getClass());
        }
    }

    @NotNull
    protected String getType(@NotNull Class<?> clazz) {
        return String.format("%s.%s", namespace, clazz.getSimpleName());
    }

    @NotNull
    public String getRequestType(@NotNull String action) {
        return String.format("%s.%s%s", namespace, action, SUFFIX_REQUEST);
    }

    @NotNull
    public String getResponseType(@NotNull String action) {
        return String.format("%s.%s%s", namespace, action, SUFFIX_RESPONSE);
    }

    @NotNull
    @Override
    public Class<?> getClassForType(@NotNull String type) throws ClassNotFoundException {
        String withReplacedSuffix;
        Optional<Class<?>> fallbackClass;

        if (endsWith(type, SUFFIX_REQUEST)) {
            withReplacedSuffix = StringUtils.replacePattern(type, Pattern.quote(SUFFIX_REQUEST), "Request");
            fallbackClass = Optional.of(ChargePointOperationRequest.class);
        } else if (endsWith(type, SUFFIX_RESPONSE)) {
            withReplacedSuffix = StringUtils.replacePattern(type, Pattern.quote(SUFFIX_RESPONSE), "Response");
            fallbackClass = Optional.of(ChargePointOperationResponse.class);
        } else {
            withReplacedSuffix = type;
            fallbackClass = Optional.empty();
        }

        String name = MESSAGE_PACKAGE + removeStart(withReplacedSuffix, namespace);
        try {
            return Thread.currentThread().getContextClassLoader()
                         .loadClass(name);
        } catch (ClassNotFoundException e) {
            // return to fallback class, if provided
            return fallbackClass.orElseThrow(() -> e);
        }
    }
}
