package ru.ifmo.testlib.io;

import ru.ifmo.testlib.*;

import java.io.File;

/**
 * An implementation of {@link AbstractInStream} for an input file.
 *
 * This implementation produces a FAIL outcome on presentation error.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public class InputInStream extends AbstractInStream {
	public InputInStream(File file) {
		super(file);
	}

	protected Outcome getExceptionForInputMismatch(String message, Exception cause) {
		return new Outcome(Outcome.Type.FAIL, message, cause);
	}
}
