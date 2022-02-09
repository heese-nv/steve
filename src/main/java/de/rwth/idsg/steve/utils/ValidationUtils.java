package de.rwth.idsg.steve.utils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ralf.heese
 */
public class ValidationUtils {

    /**
     * Validate that the specified argument character sequence is neither {@code null}, a length of zero (no characters), empty
     * nor whitespace; otherwise throwing an exception with the specified message.
     *
     * @param <T>
     *         the character sequence type
     * @param chars
     *         the character sequence to check, validated not null by this method
     * @param message
     *         the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values
     *         the optional values for the formatted exception message, null array not recommended
     * @return the validated character sequence (never {@code null} method for chaining)
     * @throws NullPointerException
     *         if the character sequence is {@code null}
     * @throws IllegalArgumentException
     *         if the character sequence is blank
     */
    @NotNull
    @Contract("null,_,_ -> fail")
    public static <T extends CharSequence> T requireNotBlank(@Nullable T chars, @NotNull String message, @Nullable Object... values)
            throws NullPointerException, IllegalArgumentException {
        if (chars == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (StringUtils.isBlank(chars)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return chars;
    }

    private ValidationUtils() {
    }
}
