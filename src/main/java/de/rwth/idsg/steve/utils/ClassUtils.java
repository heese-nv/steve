package de.rwth.idsg.steve.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import de.rwth.idsg.steve.SteveException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ralf.heese
 */
public class ClassUtils {

    /**
     * Get all classes of the package {@code packageName} implementing a specific interface. The result will not contain
     * interface classes nor abstract classes.
     *
     * @param packageName
     *         package name
     * @param interfaceClass
     *         implemented interface class
     * @return map simple class name to class
     */
    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    public static @NotNull <INTERFACE, IMPL extends INTERFACE> Map<String, Class<IMPL>> getClassesWithInterface(
            @NotNull String packageName, @NotNull Class<INTERFACE> interfaceClass) {
        try {
            ImmutableSet<ClassPath.ClassInfo> classInfos = ClassPath.from(Thread.currentThread().getContextClassLoader())
                                                                    .getTopLevelClasses(packageName);

            Map<String, Class<IMPL>> map = new HashMap<>();
            for (ClassPath.ClassInfo classInfo : classInfos) {
                Class<?> clazz = classInfo.load();
                if (interfaceClass.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                    map.put(clazz.getSimpleName(), (Class<IMPL>) clazz);
                }
            }
            return map;
        } catch (IOException e) {
            String message = String.format("Error while listing all classes in %s implementing the interface %s", packageName, interfaceClass.getName());
            throw new SteveException(message, e);
        }
    }
}
