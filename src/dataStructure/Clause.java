package dataStructure;

import java.util.HashMap;
import java.util.Vector;
import dataStructure.Variable.State;

/**
 * A clause.
 * 
 */
public class Clause {
	/* Literals of the clause */
	private Vector<Integer> literals;

	public enum ClauseState {
		SAT, EMPTY, UNIT, SUCCESS
	};

	/* 2 Beobachtete Literale */
	int lit1, lit2;

	HashMap<Integer, Variable> variables;

	/**
	 * Creates a new clause with the given literals.
	 * 
	 * @param literals
	 *            literals of the clause
	 * @param variables
	 */
	public Clause(Vector<Integer> literals, HashMap<Integer, Variable> variables) {
		this.literals = literals;
		this.variables = variables;
	}

	/**
	 * Returns the literals of this clause.
	 * 
	 * @return literals of this clause
	 */
	public Vector<Integer> getLiterals() {
		return literals;
	}

	/**
	 * Returns an unassigned literal of this clause.
	 * 
	 * @param variables
	 *            variable objects
	 * @return an unassigned literal, if one exists, 0 otherwise
	 */

	public int getUnassigned(HashMap<Integer, Variable> variables) {
		if (variables.get(lit1).getState() == State.OPEN) {
			return lit1;
		} else if (variables.get(lit2).getState() == State.OPEN) {
			return lit2;
		}
		return 0;
	}

	/**
	 * Returns the phase of the variable within this clause.
	 * 
	 * @param num
	 *            variable ID (>= 1)
	 * @return true, if variable is positive within this clause, otherwise false
	 */
	public boolean getPolarity(int num) {
		return num > 0;
	}

	/**
	 * Returns the size of this clause.
	 * 
	 * @return size of this clause.
	 */
	public int size() {
		return literals.size();
	}

	/**
	 * Initialisiert die zu beobachtenden Literale und gibt entsrpechenden
	 * Status der Clauses zurück
	 * 
	 * @param variables
	 * @return
	 */
	public ClauseState initWatch(HashMap<Integer, Variable> variables) {
		int numberOfLiterals = literals.size();
		if (numberOfLiterals == 0) {
			return ClauseState.EMPTY;
		} else if (numberOfLiterals == 1) {
			lit2 = lit1 = literals.get(0);
			variables.get(lit1).isWatchedBy(this);
			return ClauseState.UNIT;
		} else {
			lit1 = literals.get(0);
			lit2 = literals.get(1);
			variables.get(lit1).isWatchedBy(this);
			variables.get(lit2).isWatchedBy(this);
			return reWatch(variables, 0);
		}
	}

	public ClauseState reWatch(HashMap<Integer, Variable> variables, int litId) {
		if (variables.get(lit1).getState() == State.OPEN && variables.get(lit2).getState() == State.OPEN) {
			if (lit1 == lit2) {
				return ClauseState.UNIT;
			} else {
				return ClauseState.SUCCESS;
			}
		}
		if (evaulatesToTrue(lit1) || evaulatesToTrue(lit2)) {
			return ClauseState.SAT;
		}

		if (litId != Math.abs(lit1)) {
			lit1 = lit1 ^ lit2;
			lit2 = lit1 ^ lit2;
			lit1 = lit1 ^ lit2;
		}

		ClauseState lit1State = findNewOne(lit1);
		ClauseState lit2State = findNewOne(lit2);
		if (lit1State == ClauseState.SAT || lit2State == ClauseState.SAT) {
			return ClauseState.SAT;
		}
		if (lit1State == ClauseState.SUCCESS && lit2State == ClauseState.EMPTY
				|| lit2State == ClauseState.SUCCESS && lit1State == ClauseState.EMPTY) {
			return ClauseState.UNIT;
		}
		if (lit1State == ClauseState.SUCCESS && lit2State == ClauseState.SUCCESS) {
			return ClauseState.SUCCESS;
		}

		return ClauseState.EMPTY;

	}

	/**
	 * Reassign one of the watched Litearls and return the found State
	 * 
	 * @param lit1
	 *            true -> reassign lit1, else lit2
	 * @return ClauseState found for the literal
	 */
	private ClauseState findNewOne(int literal) {
		if (evaulatesToTrue(literal)) {
			return ClauseState.SAT;
		} else if (variables.get(literal).getState() == State.OPEN) {
			return ClauseState.SUCCESS;
		}

		for (int currentLiteral : literals) {
			if (currentLiteral == lit1 || currentLiteral == lit2) {
				continue;
			}

			State currentLiteralState = variables.get(currentLiteral).getState();
			if (currentLiteralState == State.OPEN) {
				removeAndReplaceWatchedLiteral(literal, currentLiteral);
				return ClauseState.SUCCESS;
			}

			if (evaulatesToTrue(currentLiteral)) {
				removeAndReplaceWatchedLiteral(literal, currentLiteral);
				return ClauseState.SAT;
			}
		}
		return ClauseState.EMPTY;
	}

	private void removeAndReplaceWatchedLiteral(int literal1, int literal2) {
		variables.get(literal1).removeWatchedBy(this);
		variables.get(literal2).isWatchedBy(this);
		if (literal1 == lit1) {
			lit1 = literal2;
		} else {
			lit2 = literal2;
		}
	}

	/**
	 * Checks if a literal is correctly assigned according to this clause NOT
	 * the literal ID, but the ACTUAL Literal
	 * 
	 * @param literal
	 * @return
	 */
	public boolean evaulatesToTrue(int literal) {
		State state = variables.get(literal).getState();
		if (state == State.OPEN) {
			return false;
		}
		if ((state == State.TRUE && getPolarity(literal)) || state == State.FALSE && !getPolarity(literal)) {
			return true;
		}
		return false;
	}

	public boolean isSat() {
		return (evaulatesToTrue(lit1) || evaulatesToTrue(lit2));
	}

	@Override
	public String toString() {
		String res = "{ ";
		for (Integer i : literals)
			res += i + "("+variables.get(i).getState() +")["+(variables.get(i).reason == null ? 'd':'p')+variables.get(i).level+"]" + (lit1 == i?"lit1":"") 
			+ (lit2==i?"lit2":"") + ", ";
		return res + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((literals == null) ? 0 : literals.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Clause other = (Clause) obj;
		if (literals == null) {
			if (other.literals != null)
				return false;
		} else if (!literals.equals(other.literals))
			return false;
		return true;
	}

}