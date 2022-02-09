package de.rwth.idsg.steve.mq.kafka.service.mapper;

import org.jetbrains.annotations.NotNull;

/**
 * Generate message types by replacing the name of the Java package with a namespace.
 *
 * @author ralf.heese
 */
public class PackageMessageTypeMapper implements MessageTypeMapper {

    private static final String MESSAGE_PACKAGE = "de.rwth.idsg.steve.mq.message";

    private final String namespace;

    public PackageMessageTypeMapper(@NotNull String namespace) {
        this.namespace = namespace;
    }

    @Override
    public @NotNull String getType(@NotNull Object o) {
        return getType(o.getClass());
    }

    @Override
    public @NotNull String getType(@NotNull Class<?> clazz) {
        return String.format("%s.%s", namespace, clazz.getSimpleName());
    }

    @Override
    public @NotNull Class<?> getClass(@NotNull String type) throws ClassNotFoundException {
        String name = MESSAGE_PACKAGE + type.replaceAll(namespace, "");
        return Thread.currentThread().getContextClassLoader()
                     .loadClass(name);
    }
}
