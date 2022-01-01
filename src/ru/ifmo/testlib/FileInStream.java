package ru.ifmo.testlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

/**
 * A file-based implementation of the {@link InStream} interface.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public class FileInStream implements InStream {
	/** A file to read data from. */
	private final File file;

	/** Current character. */
	private int currChar;

	/** A reader used to read data. */
	private BufferedReader reader;

	/** The outcome mapping to be used for this stream. */
	private final Map<Outcome.Type, Outcome.Type> outcomeMapping;

	/**
	 * Creates new {@link InStream} for specified file and with the specified outcome mapping.
	 *
	 * @param file a file to read data from
	 */
	FileInStream(File file, Map<Outcome.Type, Outcome.Type> outcomeMapping) {
		this.file = file;
		this.outcomeMapping = outcomeMapping;
		reset();
	}

	public void setOutcomeMapping(Outcome.Type from, Outcome.Type to) {
		outcomeMapping.put(from, to);
	}

	public void reset() {
		try {
			if (reader != null) {
				reader.close();
			}
			reader = new BufferedReader(new FileReader(file));
		} catch (IOException ex) {
		    // The output file might not exist, because the participant is "evil".
			throw quit(Outcome.Type.PE, "File not found: " + ex.toString());
		}
		nextChar();
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

	public boolean seekEoLn() {
		while (!isEoLn() && Character.isWhitespace(currChar)) {
            nextChar();
        }
		return isEoLn();
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

	public String nextToken(String before, String after) {
		while (!isEoF() && before.indexOf((char) currChar) >= 0) {
            nextChar();
        }
		if (isEoF()) {
			throw quit(Outcome.Type.PE, "Unexpected end of file");
		}
		StringBuilder builder = new StringBuilder();
		while (!isEoF() && after.indexOf((char) currChar) < 0) {
			builder.append((char) currChar);
            nextChar();
        }
		return builder.toString();
	}

	public String nextToken(String skip) {
		return nextToken(skip, skip);
	}

	public String nextToken() {
		return nextToken(" \t\r\n");
	}

	public int nextInt() {
		String word = nextToken();
		try {
			return Integer.parseInt(word);
		} catch (NumberFormatException ex) {
			throw quit(Outcome.Type.PE, String.format("A 32-bit signed integer expected, %s found", word), ex);
		}
	}

	public long nextLong() {
		String word = nextToken();
		try {
			return Long.parseLong(word);
		} catch (NumberFormatException ex) {
			throw quit(Outcome.Type.PE, String.format("A 64-bit signed integer expected, %s found", word), ex);
		}
	}

	public BigInteger nextBigInteger() {
		String word = nextToken();
		try {
			return new BigInteger(word);
		} catch (NumberFormatException ex) {
			throw quit(Outcome.Type.PE, String.format("An integer expected, %s found", word), ex);
		}
	}

	public float nextFloat() {
		String word = nextToken();
		try {
			return Float.parseFloat(word);
		} catch (NumberFormatException ex) {
			throw quit(Outcome.Type.PE, String.format("A float number expected, %s found", word), ex);
		}
	}

	public double nextDouble() {
		String word = nextToken();
		try {
			double v = Double.parseDouble(word);
			if (Double.isInfinite(v) || Double.isNaN(v)) {
    			throw new NumberFormatException(word);
			}
			return v;
		} catch (NumberFormatException ex) {
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

	public int nextChar() {
		try {
		    int result = currChar;
			currChar = reader.read();
            return result;
		} catch (IOException ex) {
			throw quit(Outcome.Type.PE, ex.getMessage());
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
