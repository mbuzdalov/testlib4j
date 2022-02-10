package ru.ifmo.testlib;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.Predicate;

import ru.ifmo.testlib.Outcome.Type;
import ru.ifmo.testlib.utils.CheckerUtils;

/**
 * 
 * @author Andrey Plotnikov (Shemplo)
 *
 */
public class InputStreamInStream implements InStream {
    
    /** Instance of object that reads input stream. */
    protected CharSource source;

    /** The outcome mapping to be used for this stream. */
    protected final Map <Type, Type> outcomeMapping;

    /**
     * Creates new {@link InStream} for specified file and with the specified outcome mapping.
     *
     * @param file a file to read data from
     */
    public InputStreamInStream (InputStream inputStream, Map <Type, Type> outcomeMapping) {
        source = new CharSource (inputStream);
        this.outcomeMapping = outcomeMapping;
    }
    
    public void setOutcomeMapping (Type from, Type to) {
        outcomeMapping.put (from, to);
    }

    public void reset () {
        throw new UnsupportedOperationException ();
    }

    public void close() {
        if (source != null) {
            source.close ();
        }
    }
    
    @Override
    public boolean seekEoF () {
        return source.seekEoF ();
    }
    
    @Override
    public boolean seekEoLn () {
        return source.seekEoLn ();
    }
    
    public void assertEoF (String message) throws Outcome {
        if (!source.seekEoF ()) {
            throw Outcome.quit (Type.PE, message);
        }
    }
    
    public void assertEoLn (String message) throws Outcome {
        if (!source.seekEoLn ()) {
            throw Outcome.quit (Type.PE, message);
        }
    }
    
    public String nextToken (int maxLength, boolean skipAfter) {
        source.skipWhitespaces ();
        
        return CheckerUtils.assertNonEmpty (source.takeWhile (
            c -> !Character.isWhitespace (c), 
            maxLength, skipAfter
        ));
    }
    
    public String nextToken (Predicate <Integer> before, Predicate <Integer> token, int maxLength, boolean skipAfter) {
        source.skipWhile (before);
        return CheckerUtils.assertNonEmpty (source.takeWhile (token, maxLength, skipAfter));
    }
    
    public String nextLine (boolean emptyIsAllowed) {
        final var line = source.takeLine (10000, true);
        return emptyIsAllowed ? line : CheckerUtils.assertNonEmpty (line);
    }
    
    public int nextInt () {
        final var token = nextToken ("-2147483648".length (), false);
        
        try {            
            return Integer.parseInt (token);
        } catch (NumberFormatException nfe) {
            try {
                return Integer.parseUnsignedInt (token);
            } catch (NumberFormatException nfee) {
                throw quit (Type.PE, String.format ("A 32-bit integer expected but `%s` found", token));
            }
        }
    }

    public long nextLong () {
        final var token = nextToken ("-9223372036854775808".length (), false);
        
        try {            
            return Long.parseLong (token);
        } catch (NumberFormatException nfe) {
            try {
                return Long.parseUnsignedLong (token);
            } catch (NumberFormatException nfee) {
                throw quit (Type.PE, String.format ("A 64-bit integer expected but `%s` found", token));
            }
        }
    }
    
    public BigInteger nextBigInteger (int maxTokenLength) {
        final var token = nextToken (maxTokenLength, false);
        
        try {            
            return new BigInteger (token);
        } catch (NumberFormatException nfe) {
            throw quit (Type.PE, String.format ("An integer expected but `%s` found", token));
        }
    }
    
    public float nextFloat (boolean finite) {
        final var token = nextToken (50, false);
        
        try {            
            final var value = Float.parseFloat (token);
            return finite ? CheckerUtils.assertFinite (value) : value;
        } catch (NumberFormatException nfe) {
            throw quit (Type.PE, String.format ("A float number expected but `%s` found", token));
        }
    }
    
    public double nextDouble (boolean finite) {
        final var token = nextToken (100, false);
        
        try {
            final var value = Double.parseDouble (token);
            return finite ? CheckerUtils.assertFinite (value) : value;
        } catch (NumberFormatException nfe) {
            throw quit (Type.PE, String.format ("A double number expected but `%s` found", token));
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
    public Outcome quit (Type type, String message) {
        throw new Outcome (outcomeMapping.getOrDefault (type, type), message);
    }
    
}
