package dataStructure;

import java.util.Vector;

/**
 * A variable.
 * 
 */
public class Variable {

	/* Assignment states of a variable */
	public enum State {
		TRUE, FALSE, OPEN
	};

	/* Current assignment */
	private State state;

	/* Variable ID (range from 1 to n) */
	private int id;

	/* Clauses containing this variable */
	private Vector<Clause> adjacencyList;

	/**
	 * Creates a variable with the given ID.
	 * 
	 * @param id
	 *            ID of the variable
	 */
	public Variable(int id) {
		// TODO: To implement!
	}

	/**
	 * Returns the current assignment state of this variable.
	 * 
	 * @return current assignment state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns the ID of this variable.
	 * 
	 * @return ID of this variable
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the adjacency list of this variable.
	 * 
	 * @return adjacency list of this variable
	 */
	public Vector<Clause> getAdjacencyList() {
		return adjacencyList;
	}

	/**
	 * Assigns variable with the given value and updates the internal state of
	 * the corresponding clauses.
	 * 
	 * @param val
	 *            value to be assigned
	 */
	public void assign(boolean val) {
		// TODO: To implement!
	}

	@Override
	public String toString() {
		String res = "[" + state + " ";
		res += "\n\tAdjacence List: " + adjacencyList;
		return res + "\n]";
	}
}