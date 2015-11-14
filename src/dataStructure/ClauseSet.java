package dataStructure;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import parser.DimacsParser;

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
	 * @throws IOException
	 */
	public ClauseSet(String filePath) throws IOException {
		DimacsParser dimacsParser = new DimacsParser(filePath);
		this.varNum = dimacsParser.getNumberOfVariables();
		this.clauses = new Vector<>(dimacsParser.getNumberOfClauses());
		this.variables = new HashMap<>(dimacsParser.getNumberOfVariables());
		Vector<Vector<Integer>> formula = dimacsParser.getFormula();
		for (Vector<Integer> rawClause : formula) {
			Clause clause = new Clause(rawClause, variables);
			this.clauses.add(clause);
			for (Integer literal : rawClause) {
				Variable variable = addVariable(literal);
				variable.getAdjacencyList().add(clause);
			}
		}
	}

	private Variable addVariable(Integer literal) {
		Variable variable = variables.get(literal);
		if (variable == null) {
			int variableId = Math.abs(literal);
			variable = new Variable(variableId);
			variables.put(variableId, variable);
			variables.put(-variableId, variable);
		}
		return variable;
	}

	/**
	 * Creates a ClauseSet from a some Clauses. For testing.
	 * 
	 * @param clauses
	 */
	public ClauseSet(HashMap<Integer, Variable> variables, Clause... clauses) {
		this.clauses = new Vector<>(Arrays.asList(clauses));
		this.variables = variables;
		this.varNum = variables.size();
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
			Integer literal = unit.getUnassigned(variables);
			boolean polarity = unit.getPolarity(literal);
			variables.get(Math.abs(literal)).assign(polarity);
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