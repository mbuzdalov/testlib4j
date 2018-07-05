package ru.ifmo.testlib;

/**
 * Describes an outcome. The instances of the class are immutable.
 *
 * @author Maxim Buzdalov
 * @author Andrew Stankevich
 * @author Dmitry Paraschenko
 * @author Sergey Melnikov
 */
public class Outcome extends RuntimeException {
	public static Outcome quit(Type type, String format, Object... obj) {
		throw new Outcome(type, String.format(format, obj));
	}

	/**
	 * Possible outcome types.
	 */
	public enum Type {
		OK, WA, PE, FAIL
	}

	/**
	 * A type of the outcome.
	 */
	private Type type;
	/**
	 * A comment to the outcome.
	 */
	private String comment;

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

	public Outcome(Type type, String comment, Exception ex) {
		super(ex);
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
