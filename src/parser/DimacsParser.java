package parser;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class DimacsParser {
	private int numberOfVariables;
	private int numberOfClauses;

	private Vector<Vector<Integer>> formula;

	public DimacsParser(String file) throws IOException {
		read(new FileInputStream(file));
	}

	public Vector<Vector<Integer>> getFormula() {
		return formula;
	}

	public int getNumberOfClauses() {
		return numberOfClauses;
	}

	public int getNumberOfVariables() {
		return numberOfVariables;
	}

	private void read(InputStream in) throws IOException {
		int input;
		while ((input = in.read()) != -1) {
			switch ((char) input) {
			case 'c':
				ignoreComments(in);
				break;
			case 'p':
				parseFormula(in);
				return;
			default:
				throw new IllegalStateException("Unknown state for input: " + (char) input);
			}
		}
		throw new EOFException();
	}

	/**
	 * Reads the full formula from input stream
	 * 
	 * @param in
	 *            The input stream to read from
	 * @throws IOException
	 *             {@see InputStream#read}
	 */
	private void parseFormula(InputStream in) throws IOException {
		parseHeader(in);
		parseClauses(in);
	}

	/**
	 * Reads all the comments and discards them.
	 *
	 * @param in
	 *            Input stream to read from.
	 * @throws IOException
	 *             {@see InputStream#read}
	 */
	private void ignoreComments(InputStream in) throws IOException {
		while (true) {
			if (in.read() == '\n') {
				break;
			}
		}
	}

	/**
	 * Reads the header from input stream and parses it.
	 *
	 * @param in
	 *            Input stream to read from.
	 * @throws IOException
	 *             {@see InputStream#read}
	 */
	private void parseHeader(InputStream in) throws IOException {
		// TODO: Do we ever read some thing else than a CNF?
		String type = readType(in);
		Integer numberOfVariables = readNumber(in);
		if (numberOfVariables != null) {
			this.numberOfVariables = numberOfVariables;
		} else {
			throw new IllegalArgumentException("Header: Number of variables is not readable.");
		}
		Integer numberOfClauses = readNumber(in);
		if (numberOfClauses != null) {
			this.numberOfClauses = numberOfClauses;
			this.formula = new Vector<>(this.numberOfClauses);
		} else {
			throw new IllegalArgumentException("Header: Number of clauses is not readable.");
		}
	}

	/**
	 * Reads all clauses from input stream and parses them.
	 * 
	 * @param in
	 *            Input stream to read from.
	 * @throws IOException
	 *             {@see InputStream#read}
	 */
	private void parseClauses(InputStream in) throws IOException {
		Vector<Integer> clause = new Vector<>();
		Integer number;
		while ((number = readNumber(in)) != null) {
			int variable = number;
			if (variable == 0) {
				formula.add(clause);
				clause = new Vector<>();
			} else {
				clause.add(variable);
			}
		}
		if (!clause.isEmpty()) {
			formula.add(clause);
		}
	}

	/**
	 * Reads a number from input stream by first skipping all white spaces and
	 * then reading some characters until the next whitespace occurs.
	 *
	 * @param in
	 *            Input stream to read from.
	 * @return The read number or NULL on EOF.
	 * @throws IOException
	 *             {@see InputStream#read}
	 */
	private Integer readNumber(InputStream in) throws IOException {
		StringBuilder buffer = new StringBuilder();
		char c = skipSpaces(in);
		if (!isValidLiteralCharacter(c)) {
			return null;
		}
		buffer.append(c);

		int input;
		while ((input = in.read()) != -1) {
			c = (char) input;
			if (Character.isDigit(c)) {
				buffer.append(c);
			} else {
				break;
			}
		}
		return Integer.parseInt(buffer.toString());
	}

	private boolean isValidLiteralCharacter(char c) {
		return c == '-' || Character.isDigit(c);
	}

	/**
	 * Reads the three character type string from input stream.
	 *
	 * @param in
	 *            Input stream to read from.
	 * @return The type string.
	 * @throws IOException
	 *             {@see InputStream#read}
	 */
	private String readType(InputStream in) throws IOException {
		return String.valueOf(skipSpaces(in)) + (char) in.read() + (char) in.read();
	}

	/**
	 * Skips all white spaces while reading from input stream in. The first
	 * non-whitespace character will be returned.
	 * <p>
	 * {@see Character#isWhitespace}
	 *
	 * @param in
	 *            Input stream to read from.
	 * @return the first non-whitespace character.
	 * @throws IOException
	 *             {@see InputStream#read}
	 */
	private char skipSpaces(InputStream in) throws IOException {
		int input;
		char c = 0;
		while ((input = in.read()) != -1) {
			c = (char) input;
			if (!Character.isWhitespace(c)) {
				return c;
			}
		}
		return c;
	}
}
