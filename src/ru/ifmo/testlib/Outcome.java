package ru.ifmo.testlib;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Describes an outcome. The instances of the class are immutable.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public class Outcome extends RuntimeException {
    /**
     * Throws a new outcome with the given type and message composed from the given format string and arguments.
     *
     * @param type the type of the outcome.
     * @param formatString the format string for the message to be specified in the outcome.
     * @param arguments the arguments for the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    public static Outcome quit(Type type, String formatString, Object... arguments) {
        throw new Outcome(type, String.format(formatString, arguments));
    }

    /**
     * Throws a new outcome with the given type and message composed from the given format string and arguments.
     *
     * @param points outcome points.
     * @param formatString the format string for the message to be specified in the outcome.
     * @param arguments the arguments for the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    public static Outcome quitp(double points, String formatString, Object... arguments) {
        throw new PointsOutcome(points, String.format(formatString, arguments));
    }

    /**
     * Throws a new outcome with the given type and message composed from the given format string and arguments.
     *
     * @param points outcome points.
     * @param formatString the format string for the message to be specified in the outcome.
     * @param arguments the arguments for the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    public static Outcome quitp(int points, String formatString, Object... arguments) {
        throw new PointsOutcome(points, String.format(formatString, arguments));
    }

    /**
     * Possible outcome types.
     */
    public enum Type {
        OK, WA, PE, FAIL, POINTS
    }

    /**
     * An outcome remapping used for input and answer {@link InStream}s.
     */
    static final Map<Type, Type> nonOkayIsFail;

    static {
        EnumMap<Type, Type> result = new EnumMap<>(Type.class);
        result.put(Type.WA, Type.FAIL);
        result.put(Type.PE, Type.FAIL);
        nonOkayIsFail = Collections.unmodifiableMap(result);
    }

    /**
     * A type of the outcome.
     */
    private final Type type;
    /**
     * A comment to the outcome.
     */
    private final String comment;

    /**
     * Creates a new outcome.
     *
     * @param type the type of the outcome.
     * @param comment the comment to the outcome.
     */
    public Outcome(Type type, String comment) {
        this.type = type;
        this.comment = comment;
    }

    /**
     * Returns the type of the outcome.
     *
     * @return the type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the comment to the outcome.
     *
     * @return the comment.
     */
    public String getComment() {
        return comment;
    }
}
