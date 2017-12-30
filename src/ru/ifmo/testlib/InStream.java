package ru.ifmo.testlib;

import java.math.BigInteger;

/**
 * InStream
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public interface InStream {
    /**
     * A special value denoting end of file.
     */
    int EOF_CHAR = -1;

    /**
     * Returns the next character from the stream, of {@link #EOF_CHAR} if end of file is reached.
     * Moves the current position in the stream to the next character.
     *
     * @return next character from the stream
     */
    int nextChar();

    /**
     * Returns the next character for the input stream.
     * If end of file is reached, returns {@link #EOF_CHAR}.
     * Doesn't move the current position -
     * the following {#nextChar()} or {#currChar()} call returns the same character.
     *
     * @return current character for the stream
     */
    int currChar();

    /**
     * Returns whether the end of file was reached.
     *
     * @return whether the end of file was reached.
     */
    boolean isEoF();

    /**
     * Returns whether the end of line was reached.
     *
     * @return whether the end of line was reached.
     */
    boolean isEoLn();

    /**
     * Skips spaces and tab-characters and returns whether the end of file was reached.
     *
     * @return whether the end of file was reached after skipping all spaces and tab-characters.
     */
    boolean seekEoF();

    /**
     * Skips spaces and tab-characters and returns whether the end of line was reached.
     *
     * @return whether the end of line was reached after skipping all spaces and tab-characters.
     */
    boolean seekEoLn();

    /**
     * Skips current line.
     */
    void skipLine();

    /**
     * Skips all characters from specified string.
     *
     * @param skip the {@link String} containing characters to be skipped.
     */
    void skip(String skip);

    /**
     * Resets the stream.
     */
    void reset();

    /**
     * Closes the stream.
     */
    void close();

    /**
     * Returns next {@link String} token with specified delimiters.
     *
     * @param before the {@link String} containing characters to be used as delimiters before the token.
     * @param after the {@link String} containing characters to be used as delimiters after the token.
     *  
     * @return next {@link String} token;
     */
    String nextToken(String before, String after);

    /**
     * Returns next {@link String} token with specified delimiters.
     *
     * @param skip the {@link String} containing characters to be used as delimiters before and after the token.
     *
     * @return next {@link String} token;
     */
    String nextToken(String skip);

    /**
     * Returns next {@link String} token. Whitespace characters are used as delimiters.
     * 
     * @return next {@link String} token.
     */
    String nextToken();

    /**
     * Returns next {@link Integer} number. Whitespace characters are used as delimiters.
     *
     * @return next {@link Integer} number.
     */
    int nextInt();

    /**
     * Returns next {@link Long} number. Whitespace characters are used as delimiters.
     *
     * @return next {@link Long} number.
     */
    long nextLong();

    /**
     * Returns next {@link BigInteger} number. Whitespace characters are used as delimiters.
     *
     * @return next {@link BigInteger} number.
     */
    BigInteger nextBigInteger();

    /**
     * Returns next {@link Float} number. Whitespace characters are used as delimiters.
     *
     * @return next {@link Float} number.
     */
    float nextFloat();

    /**
     * Returns next {@link Double} number. Whitespace characters are used as delimiters.
     *
     * @return next {@link Double} number.
     */
    double nextDouble();

    /**
     * Returns unread part of current line.
     *
     * @return unread part of current line.
     */
    String nextLine();
}
