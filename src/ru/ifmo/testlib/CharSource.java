package ru.ifmo.testlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

import ru.ifmo.testlib.Outcome.Type;

/**
 * 
 * This class is responsible for reading of passed input stream.
 * It has methods for raw access to currently read character like {@link #currChar()}
 * and also high-level methods (like {@link #takeWhile(Predicate)}) for reading tokens 
 * - sequence of characters from some set that are separated with another set of characters (delimiters).
 * 
 * @author Andrey Plotnikov (Shemplo)
 *
 */
public class CharSource {
    
    /**
     * A special value denoting end of file.
     */
    private static final int EOF_CHAR = -1;
    
    /** Current character. */
    protected int currChar;

    /** A reader used to read data. */
    protected BufferedReader reader;
    
    public CharSource (InputStream inputStream) {
        if (inputStream != null) {            
            reader = new BufferedReader (new InputStreamReader (
                inputStream, StandardCharsets.UTF_8
            ));
            
            nextChar ();
        }
    }
    
    public void close() {
        try {
            if (reader != null) {                
                reader.close();
            }
        } catch (IOException ex) {
            // Even if the participant is totally "evil", this must not happen
            throw new Outcome (Outcome.Type.FAIL, "Cannot close file: " + ex.toString());
        }
    }
    
    /**
     * Returns the next character for the input stream.
     * If end of file is reached, returns {@link #EOF_CHAR}.
     * Doesn't move the current position -
     * the following {#nextChar()} or {#currChar()} call returns the same character.
     *
     * @return current character for the stream
     */
    public int currChar () {
        return currChar;
    }
    
    /**
     * Returns whether the end of file was reached.
     *
     * @return whether the end of file was reached.
     */
    public boolean isEoF () {
        return currChar == EOF_CHAR;
    }
    
    /**
     * Returns whether the end of line was reached.
     *
     * @return whether the end of line was reached.
     */
    public boolean isEoLn () {
        return isEoF () || currChar == '\r' || currChar == '\n';
    }
    
    /**
     * Returns the next character from the stream, of {@link #EOF_CHAR} if end of file is reached.
     * Moves the current position in the stream to the next character.
     *
     * @return next character from the stream
     */
    public int nextChar () {
        try {
            final var result = currChar;
            currChar = reader.read ();
            return result;
        } catch (IOException ex) {
            throw new Outcome (Type.PE, ex.getMessage ());
        }
    }
    
    /**
     * Skips sequence of characters that are accepted by predicate until end of file is reached 
     * or current character is not appropriate for criteria.
     * 
     * @param criteria predicate defines set of characters which should be skipped
     */
    public boolean skipWhile (Predicate <Integer> criteria) {
        boolean skipped = false;
        while (!isEoF () && criteria.test (currChar)) {
            skipped = true;
            nextChar ();
        }
        
        return skipped;
    }
    
    public boolean skipWhitespaces () {
        return skipWhile (Character::isWhitespace);
    }
    
    public boolean skipWhitespacesInLine () {
        return skipWhile (c -> Character.isWhitespace (c) && Character.getType (c) != Character.CONTROL);
    }
    
    /**
     * Skips current line.
     */
    public boolean skipLine () {
        skipWhile (c -> Character.getType (c) != Character.CONTROL);
        return skipWhile (c -> Character.getType (c) == Character.CONTROL); // to handle Windows \r\n
    }
    
    /**
     * Skips spaces and tab-characters and returns whether the end of file was reached.
     *
     * @return whether the end of file was reached after skipping all spaces and tab-characters.
     */
    public boolean seekEoF () {
        skipWhitespaces ();
        return isEoF ();
    }
    
    /**
     * Skips spaces and tab-characters within line and returns whether the end of line was reached.
     *
     * @return whether the end of line was reached after skipping all spaces and tab-characters.
     */
    public boolean seekEoLn () {
        skipWhitespacesInLine ();
        return isEoLn ();
    }
    
    public String takeWhile (Predicate <Integer> criteria) {
        return takeWhile (criteria, Integer.MAX_VALUE, false);
    }
    
    public String takeWhile (Predicate <Integer> criteria, int maxLength, boolean skipAfter) {
        int max = maxLength;
        if (maxLength < 0) {
            throw new IllegalArgumentException ("Maximum length should be positive number");
        }
        
        final var sb = new StringBuilder ();
        while (!isEoF () && criteria.test (currChar) && maxLength-- > 0) {
            sb.append ((char) currChar);
            nextChar ();
        }
        
        if (skipWhile (criteria) && !skipAfter) {
            throw new Outcome (Type.PE, String.format (
                "Substring of length up to %d characters was expected", 
                max
            ));
        }
        
        return sb.toString ();
    }
    
    public String takeLine (int maxLength, boolean skipAfter) {
        final var value = takeWhile (c -> Character.getType (c) != Character.CONTROL, maxLength, skipAfter);
        skipWhile (c -> Character.getType (c) == Character.CONTROL); // to handle Windows \r\n
        return value;
    }
    
}
