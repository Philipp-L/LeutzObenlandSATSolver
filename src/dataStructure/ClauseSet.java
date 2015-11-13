package dataStructure;

import java.util.Arrays;
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
	 * Creates a ClauseSet from a some Clauses.
	 * For testing.
	 * @param clauses
	 */
	public ClauseSet(Clause... clauses) {
		this.clauses = new Vector<>(Arrays.asList(clauses));
		this.variables = new HashMap<>();
		for (Clause clause : clauses) {
			for (Integer literal : clause.getLiterals()) {
				if (!variables.containsKey(Math.abs(literal))) {
					Variable variable = new Variable(Math.abs(literal));
					variables.put(Math.abs(literal), variable);
				}
			}
		}
	}

	/**
	 * Executes unit propagation and checks for the existence of an empty
	 * clause.
	 * 
	 * @return true, if an empty clause exists, otherwise false.
	 */
	public boolean unitPropagation() {
		Clause unit;
		while ((unit = nextUnit()) != null) {
			Integer literal = unit.getLiterals().get(0);
			boolean polarity = unit.getPolarity(0);
			variables.get(literal).assign(polarity);
			unit.setSat(true);
			clauses.remove(unit);
			for (int i = this.clauses.size() - 1; i >= 0; --i) {
				Clause clause = this.clauses.get(i);
				for (int j = clause.getLiterals().size() - 1; j >= 0; --j) {
					Integer clauseLiteral = clause.getLiterals().get(j);
					if (literal.intValue() == clauseLiteral.intValue()) {
						clause.setSat(true);
						clause.setNumUnassigned(clause.getNumUnassigned() -1);
						this.clauses.remove(i);
						break;
					} else if (literal.intValue() == -clauseLiteral.intValue()) {
						clause.getLiterals().remove(j);
						clause.setNumUnassigned(clause.getNumUnassigned() -1);
						// TODO: Should we break here? Is it possible to have
						// one literal two times in one clause?
					}
				}
			}
		}
		return containsEmpty();
	}

	/**
	 * Returns the next unit clause, if one exists.
	 * 
	 * @return next unit clause, if one exists, otherwise null
	 */
	private Clause nextUnit() {
		for (Clause clause : clauses) {
			if (clause.isUnit()) {
				return clause;
			}
		}
		return null;
	}

	/**
	 * Checks, if an empty clause exists.
	 * 
	 * @return true, if an empty clause exists, otherwise false.
	 */
	private boolean containsEmpty() {
		for (Clause clause : clauses) {
			if (clause.isEmpty()) {
				clause.setSat(false);
				return true;
			}
		}
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