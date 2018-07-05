package ru.ifmo.testlib;

import java.io.*;
import java.math.*;

/**
 * Abstract implementation of {@link InStream} interface.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public abstract class AbstractInStream implements InStream {
	/** A file to read data from. */
	private File file;

	/** Current character. */
	private int currChar;

	/** A reader used to read data. */
	private BufferedReader reader;

	/**
	 * Creates new {@link InStream} for specified file.
	 *
	 * @param file a file to read data from
	 */
	public AbstractInStream(File file) {
		this.file = file;
		reset();
	}

	public void reset() {
		try {
			if (reader != null) {
				reader.close();
			}
			reader = new BufferedReader(new FileReader(file));
		} catch (IOException ex) {
		    // The output file might not exist, because the participant is "evil".
			throw getExceptionForInputMismatch("File not found", ex);
		}
		scanChar();
	}

	public void close() {
		try {
			reader.close();
		} catch (IOException ex) {
			// Even if the participant is totally "evil", this must not happen
			throw new Outcome(Outcome.Type.FAIL, "Cannot close file", ex);
		}
	}

	public int currChar() {
		return currChar;
	}

	public int nextChar() {
		scanChar();
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
			throw getExceptionForInputMismatch("Unexpected end of file");
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
			throw getExceptionForInputMismatch(String.format("A 32-bit signed integer expected, %s found", word), ex);
		}
	}

	public long nextLong() {
		String word = nextToken();
		try {
			return Long.parseLong(word);
		} catch (NumberFormatException ex) {
			throw getExceptionForInputMismatch(String.format("A 64-bit signed integer expected, %s found", word), ex);
		}
	}

	public BigInteger nextBigInteger() {
		String word = nextToken();
		try {
			return new BigInteger(word);
		} catch (NumberFormatException ex) {
			throw getExceptionForInputMismatch(String.format("An integer expected, %s found", word), ex);
		}
	}

	public float nextFloat() {
		String word = nextToken();
		try {
			return Float.parseFloat(word);
		} catch (NumberFormatException ex) {
			throw getExceptionForInputMismatch(String.format("A float number expected, %s found", word), ex);
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
			throw getExceptionForInputMismatch(String.format("A double number expected, %s found", word), ex);
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

	/**
	 * Returns the next character from the stream. Used by {@see nextChar()} and similar methods.
	 *
	 * @return next character or {@see EOF_CHAR} if the end of file was reached
	 */
	private int scanChar() {
		try {
			currChar = reader.read();
			if (currChar == -1) {
				currChar = EOF_CHAR;
			}
			return currChar;
		} catch (IOException ex) {
			throw getExceptionForInputMismatch(ex);
		}
	}

	/**
	 * Returns a correct exception when input mismatch occurs. Different types
	 * of input streams generate different exceptions on crash.
	 *
	 * @param message exception message (may be null).
	 * @param cause exception cause (may be null).
	 *
	 * @return the exception.
	 */
	protected abstract Outcome getExceptionForInputMismatch(String message, Exception cause);

	/**
	 * Returns a correct exception when input mismatch occurs. Different types
	 * of input streams generate different exceptions on crash.
	 *
	 * @param message exception message (may be null).
	 *
	 * @return the exception.
	 */
	private Outcome getExceptionForInputMismatch(String message) {
		return getExceptionForInputMismatch(message, null);
	}

	/**
	 * Returns a correct exception when input mismatch occurs. Different types
	 * of input streams generate different exceptions on crash.
	 *
	 * @param cause exception cause (may be null).
	 *
	 * @return the exception.
	 */
	private Outcome getExceptionForInputMismatch(Exception cause) {
		return getExceptionForInputMismatch(null, cause);
	}
}
