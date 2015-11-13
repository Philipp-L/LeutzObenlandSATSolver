package dataStructure;

import java.util.HashMap;
import java.util.Vector;

//import praxisblatt02.parser.DIMACSReader;

/**
 * A set of clauses.
 * 
 */
public class ClauseSet {
	/* Number of variables */
	private int varNum;

	/* Clauses of this set */
	private Vector<Clause> clauses;

	/* List of all variables */
	private HashMap<Integer, Variable> variables;

	/**
	 * Constructs a clause set from the given DIMACS file.
	 * 
	 * @param filePath
	 *            file path of the DIMACS file.
	 */
	public ClauseSet(String filePath) {
		// TODO: to implement!
	}

	/**
	 * Executes unit propagation and checks for the existence of an empty
	 * clause.
	 * 
	 * @return true, if an empty clause exists, otherwise false.
	 */
	public boolean unitPropagation() {
		// TODO: to implement!
		return false;
	}

	/**
	 * Returns the next unit clause, if one exists.
	 * 
	 * @return next unit clause, if one exists, otherwise null
	 */
	private Clause nextUnit() {
		// TODO: to implement!
		return null;
	}

	/**
	 * Checks, if an empty clause exists.
	 * 
	 * @return true, if an empty clause exists, otherwise false.
	 */
	private boolean containsEmpty() {
		// TODO: to implement!
		return false;
	}

	@Override
	public String toString() {
		return clausesToString() + "\n\n" + varsToString();
	}

	/**
	 * Returns all clauses as string representation.
	 * 
	 * @return a string representation of all clauses.
	 */
	public String clausesToString() {
		String res = "";
		for (Clause clause : clauses)
			res += clause + "\n";
		return res;
	}

	/**
	 * Returns all variables as string representation.
	 * 
	 * @return a string representation of all variables.
	 */
	public String varsToString() {
		String res = "";
		for (int i = 1; i <= varNum; i++)
			res += "Variable " + i + ": " + variables.get(i) + "\n\n";
		return res;
	}
}