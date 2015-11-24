package dataStructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import dataStructure.Clause.ClauseState;
import parser.DimacsParser;

/**
 * A set of clauses.
 * 
 */
public class ClauseSet {
	/* Number of variables */
	private int varNum;
	/* Clauses of this set */

	private Vector<Clause> clauses;

	/* Unit clauses of this list */
	private Vector<Clause> units;

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
		this.variables = new HashMap<>(dimacsParser.getNumberOfVariables());
		this.varNum = dimacsParser.getNumberOfVariables();
		this.units = new Vector<>();
		
		Vector<Clause> clauses = new Vector<>(dimacsParser.getNumberOfClauses());
		for (Vector<Integer> rawClause : dimacsParser.getFormula()) {
			Clause clause = new Clause(rawClause, variables);
			clauses.add(clause);
			for (Integer literal : rawClause) {
				addVariable(literal);
			}
		}
		init(clauses.toArray(new Clause[this.clauses.size()]));
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
		this.variables = variables;
		this.clauses = new Vector<>(clauses.length);
		this.units = new Vector<>();
		this.varNum = variables.size();
		init(clauses);
	}
	
	private void init(Clause[] clauses) {
		for (Clause currentClause : clauses) {
			ClauseState currentState = currentClause.initWatch(variables);
			if (currentState == ClauseState.UNIT) {
				this.units.addElement(currentClause);
				this.clauses.addElement(currentClause);
			} else if (currentState == ClauseState.EMPTY) {
				continue;
			} else {
				this.clauses.addElement(currentClause);
			}
		}
	}

	/**
	 * Executes unit propagation and checks for the existence of an empty
	 * clause.
	 * 
	 * @return null if no empty clause exists or the empty clause.
	 */
	public Clause unitPropagation() {
		if (units.size() == 0) {
			return null;
		}
		Vector<Clause> newUnits = new Vector<Clause>();
		for (Clause currentClause : units) {
			int currentUnassigned = currentClause.getUnassigned(variables);
			boolean polarity = currentClause.getPolarity(currentUnassigned);
			Clause emptyClause = variables.get(currentUnassigned).assign(polarity, variables, newUnits);
			if (emptyClause != null) {
				return emptyClause;
			}
		}
		units.addAll(newUnits);
		return unitPropagation();
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