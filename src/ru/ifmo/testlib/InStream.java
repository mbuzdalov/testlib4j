package ru.ifmo.testlib;

import java.io.Closeable;
import java.math.BigInteger;
import java.util.function.Predicate;

import ru.ifmo.testlib.Outcome.Type;

/**
 * An interface for reading input files.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 * @author Andrey Plotnikov (Shemplo)
 */
public interface InStream extends Closeable {
    
    boolean seekEoF ();
    
    boolean seekEoLn ();
    
    void assertEoF (String message) throws Outcome;
    
    void assertEoLn (String message) throws Outcome;

    /**
     * Resets the stream.
     */
    void reset ();

    /**
     * Closes the stream.
     */
    void close ();

    /**
     * Returns next {@link String} token. Whitespace characters are used as delimiters.
     *
     * @return next {@link String} token.
     */
    default String nextToken () {
        return nextToken (Integer.MAX_VALUE, false);
    }
    
    String nextToken (int maxLength, boolean skipAfter);
    
    String nextToken (Predicate <Integer> before, Predicate <Integer> token, int maxLength, boolean skipAfter);
    
    /**
     * Returns unread part of current line.
     *
     * @return unread part of current line.
     */
    default String nextLine () {
        return nextLine (true);
    }
    
    String nextLine (boolean emptyIsAllowed);

    /**
     * Returns next {@code int}. Whitespace characters are used as delimiters.
     *
     * @return next {@code int}.
     */
    int nextInt ();

    /**
     * Returns next {@code long}. Whitespace characters are used as delimiters.
     *
     * @return next {@code long}.
     */
    long nextLong ();

    /**
     * Returns next {@link BigInteger}. Whitespace characters are used as delimiters.
     *
     * @return next {@link BigInteger}.
     */
    BigInteger nextBigInteger (int maxTokenLength);

    /**
     * Returns next {@code float}. Whitespace characters are used as delimiters.
     *
     * @return next {@code float}.
     */
    float nextFloat (boolean finite);

    /**
     * Returns next {@code double}. Whitespace characters are used as delimiters.
     *
     * @return next {@code double}.
     */
    double nextDouble (boolean finite);

    /**
     * Throws a new outcome with the given type and message,
     * where the type is adjusted in order to match the semantics of this particular stream.
     *
     * @param type the type of the outcome.
     * @param message the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    Outcome quit (Type type, String message);

    /**
     * Throws a new outcome with the given type and message composed from the given format string and arguments,
     * where the type is adjusted in order to match the semantics of this particular stream.
     *
     * @param type the type of the outcome.
     * @param formatString the format string for the message to be specified in the outcome.
     * @param arguments the arguments for the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    default Outcome quit (Type type, String formatString, Object ... arguments) {
        throw quit (type, String.format (formatString, arguments));
    }

    /**
     * Throws a new outcome with the given type and message,
     * where the type is adjusted in order to match the semantics of this particular stream.
     *
     * @param points outcome points.
     * @param message the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    default Outcome quitp (double points, String message) {
        throw new PointsOutcome (points, message);
    }

    /**
     * Throws a new outcome with the given type and message composed from the given format string and arguments,
     * where the type is adjusted in order to match the semantics of this particular stream.
     *
     * @param points outcome points.
     * @param formatString the format string for the message to be specified in the outcome.
     * @param arguments the arguments for the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    default Outcome quitp (double points, String formatString, Object ... arguments) {
        throw quitp (points, String.format (formatString, arguments));
    }

    /**
     * Throws a new outcome with the given type and message,
     * where the type is adjusted in order to match the semantics of this particular stream.
     *
     * @param points outcome points.
     * @param message the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    default Outcome quitp (int points, String message) {
        throw new PointsOutcome (points, message);
    }

    /**
     * Throws a new outcome with the given type and message composed from the given format string and arguments,
     * where the type is adjusted in order to match the semantics of this particular stream.
     *
     * @param points outcome points.
     * @param formatString the format string for the message to be specified in the outcome.
     * @param arguments the arguments for the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    default Outcome quitp (int points, String formatString, Object ... arguments) {
        throw quitp (points, String.format (formatString, arguments));
    }

    void setOutcomeMapping (Type from, Type to);
}
