package ru.ifmo.testlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import ru.ifmo.testlib.Outcome.Type;

/**
 * 
 * @author Shemplo
 *
 */
public class InputStreamInStream implements InStream {

    /** Current character. */
    protected int currChar;

    /** A reader used to read data. */
    protected BufferedReader reader;

    /** The outcome mapping to be used for this stream. */
    protected final Map <Type, Type> outcomeMapping;

    /**
     * Creates new {@link InStream} for specified file and with the specified outcome mapping.
     *
     * @param file a file to read data from
     */
    public InputStreamInStream (InputStream inputStream, Map <Type, Type> outcomeMapping) {
        if (inputStream != null) {            
            reader = new BufferedReader (new InputStreamReader (
                inputStream, StandardCharsets.UTF_8
            ));
            
            try {
                currChar = reader.read ();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.outcomeMapping = outcomeMapping;
    }
    
    public void setOutcomeMapping (Type from, Type to) {
        outcomeMapping.put (from, to);
    }

    public void reset () {
        throw new UnsupportedOperationException ();
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException ex) {
            // Even if the participant is totally "evil", this must not happen
            throw quit(Outcome.Type.FAIL, "Cannot close file: " + ex.toString());
        }
    }

    public int currChar() {
        return currChar;
    }

    public boolean isEoF() {
        return currChar == EOF_CHAR;
    }

    public boolean isEoLn() {
        return isEoF() || currChar == '\r' || currChar == '\n';
    }

    public boolean seekEoF() {
        while (!isEoF() && Character.isWhitespace(currChar)) {
            nextChar();
        }
        return isEoF();
    }
    
    public void assertEoF (String message) throws Outcome {
        if (!seekEoF ()) {
            throw Outcome.quit (Type.PE, message);
        }
    }

    public boolean seekEoLn() {
        while (!isEoLn() && Character.isWhitespace(currChar)) {
            nextChar();
        }
        return isEoLn();
    }
    
    public void assertEoLn (String message) throws Outcome {
        if (!seekEoLn ()) {
            throw Outcome.quit (Type.PE, message);
        }
    }

    public void skipLine() {
        while (!isEoLn()) {
            nextChar();
        }
        if (currChar == '\r') nextChar();
        if (currChar == '\n') nextChar();
    }

    public void skip(String skip) {
        while (!isEoF() && skip.indexOf((char) currChar) >= 0) {
            nextChar();
        }
    }

    public String nextToken (String before, String after) {
        return nextToken (before, after, Type.PE);
    }
    
    public String nextToken (String before, String after, Type type) {
        while (!isEoF () && before.indexOf ((char) currChar) >= 0) {
            nextChar ();
        }
        if (isEoF ()) {
            throw quit (type, "Unexpected end of file");
        }
        StringBuilder builder = new StringBuilder ();
        while (!isEoF () && after.indexOf ((char) currChar) < 0) {
            builder.append ((char) currChar);
            nextChar ();
        }
        return builder.toString ();
    }

    public String nextToken(String skip) {
        return nextToken(skip, skip);
    }

    public String nextToken() {
        return nextToken(" \t\r\n");
    }

    public int nextInt () {
        return nextInt (Type.PE);
    }
    
    public int nextInt (Type type) {
        skip (" \t\r\n");
        
        final var sb = new StringBuilder ();
        if (currChar == '-') {
            sb.append ((char) currChar);
            nextChar ();
        }
        while (!isEoLn () && Character.isDigit (currChar)) {
            sb.append ((char) currChar);
            nextChar ();
        }
        
        if (!isEoLn () && !Character.isWhitespace (currChar)) {
            String found = sb.toString ();
            if (sb.length () > 100) {
                found = sb.substring (0, 100) + "...";
            }
            
            throw quit (type, String.format("A 32-bit signed integer expected, %s found", found + ((char) currChar)));
        } else if (sb.length () == 0) {
            throw quit (type, String.format("A 32-bit signed integer expected but nothing found"));
        }
        
        try {            
            return Integer.parseInt (sb.toString ());
        } catch (NumberFormatException nfe) {
            try {
                return Integer.parseUnsignedInt (sb.toString ());
            } catch (NumberFormatException nfee) {
                throw quit (type, String.format("A 32-bit signed integer expected but a larger number found"));
            }
        }
    }

    public long nextLong () {
        return nextLong (Type.PE);
    }
    
    public long nextLong (Type type) {
        String word = nextToken ();
        try {
            return Long.parseLong (word);
        } catch (NumberFormatException ex) {
            word = word.length () > 100 ? word.substring (100) : word;
            throw quit (type, String.format ("A 64-bit signed integer expected, %s found", word), ex);
        }
    }
    
    public BigInteger nextBigInteger () {
        return nextBigInteger (Type.PE);
    }

    public BigInteger nextBigInteger (Type type) {
        String word = nextToken();
        try {
            return new BigInteger(word);
        } catch (NumberFormatException ex) {
            word = word.length () > 100 ? word.substring (100) : word;
            throw quit(type, String.format("An integer expected, %s found", word), ex);
        }
    }

    public float nextFloat () {
        return nextFloat (Type.PE);
    }
    
    public float nextFloat (Type type) {
        String word = nextToken();
        try {
            return Float.parseFloat(word);
        } catch (NumberFormatException ex) {
            word = word.length () > 100 ? word.substring (100) : word;
            throw quit(type, String.format("A float number expected, %s found", word), ex);
        }
    }

    public double nextDouble () {
        return nextDouble (Type.PE);
    }
    
    public double nextDouble (Type type) {
        String word = nextToken();
        try {
            double v = Double.parseDouble(word);
            if (Double.isInfinite(v) || Double.isNaN(v)) {
                throw new NumberFormatException(word);
            }
            return v;
        } catch (NumberFormatException ex) {
            word = word.length () > 100 ? word.substring (100) : word;
            throw quit(Outcome.Type.PE, String.format("A double number expected, %s found", word), ex);
        }
    }

    public String nextLine() {
        StringBuilder sb = new StringBuilder();
        while (!isEoLn()) {
            sb.append((char) (currChar));
            nextChar();
        }
        if (currChar == '\r') nextChar();
        if (currChar == '\n') nextChar();

        return sb.toString();
    }

    public int nextChar () {
        return nextChar (Type.PE);
    }
    
    public int nextChar (Type type) {
        try {
            int result = currChar;
            currChar = reader.read();
            return result;
        } catch (IOException ex) {
            throw quit (type, ex.getMessage());
        }
    }

    /**
     * Throws a new outcome with the given type and message,
     * where the type is adjusted in order to match the semantics of this particular stream.
     *
     * @param type the type of the outcome.
     * @param message the message to be specified in the outcome.
     * @return the newly created outcome (actually it is thrown, but you can safely say {@code return quit(...)}.
     * @throws Outcome the newly created outcome.
     */
    public Outcome quit(Outcome.Type type, String message) {
        throw new Outcome(outcomeMapping.getOrDefault(type, type), message);
    }
    
}
