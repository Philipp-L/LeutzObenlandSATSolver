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
	 * Status der Clauses zurÃ¼ck
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
			return ClauseState.SUCCESS;
		}
	}

	public ClauseState reWatch(HashMap<Integer, Variable> variables, int litId) {
		//Neuer watch war irelevant
		if(litId != Math.abs(lit1 )&& litId != Math.abs(lit2)){
			return ClauseState.SUCCESS;
		}
		
		//einfacheit - tausche ersetzes lit an lit 1
		if (litId != Math.abs(lit1) ) {
			lit1 = lit1 ^ lit2;
			lit2 = lit1 ^ lit2;
			lit1 = lit1 ^ lit2;
		}

		// die klausel is jetzt sat
		if(variables.get(lit1).getState() == State.TRUE && getPolarity(lit1) == true || 
			variables.get(lit2).getState() == State.FALSE && getPolarity(lit1) == false){
			variables.get(litId).removeWatchedBy(this);
			return ClauseState.SAT;
		}

		// find new watched literal - alle auser bereits vergebene auf Open testen
		for (int currentLiteral : literals) {
			boolean currentPolarity = getPolarity(currentLiteral);
			
			if ((currentLiteral == lit1) || (currentLiteral == lit2)) {
				continue;
			}
			State currentVariableState = variables.get(currentLiteral).getState();
			//neuer opne gefunden - alles gut!
			if ((currentVariableState == State.OPEN)) {
				lit1 = currentLiteral;
				variables.get(lit1).isWatchedBy(this);
				variables.get(litId).removeWatchedBy(this);

				return ClauseState.SUCCESS;
			}
			
			//geteste Variable is zwar nicht open, aber dafür sat - sollte eigentlich nicht vorkommen
			if((currentVariableState == State.TRUE && currentPolarity) ||
				currentVariableState == State.FALSE && !currentPolarity){
				variables.get(litId).removeWatchedBy(this);
				return ClauseState.SAT;
			}
		}
		// kein neues watched Literal gefunden
		variables.get(litId).removeWatchedBy(this);
		lit1 = lit2;
		boolean lit2Polarity = getPolarity(lit2);
		State lit2State = variables.get(lit2).getState();
		if ((lit2State == State.FALSE && lit2Polarity) || (lit2State == State.TRUE && !lit2Polarity)) {
			variables.get(litId).removeWatchedBy(this);
			return ClauseState.EMPTY;
		} else if ((lit2State == State.FALSE && !lit2Polarity) || (lit2State == State.TRUE && lit2Polarity)) {
			variables.get(litId).removeWatchedBy(this);
			return ClauseState.SAT;
		} else if (lit2State == State.OPEN) {
			return ClauseState.UNIT;
		}
		return null;
	}

	public boolean isSat(){
		State state1 = variables.get(lit1).getState();
		State state2 = variables.get(lit2).getState();
		boolean polarity1 = getPolarity(lit1);
		boolean polarity2 = getPolarity(lit2);
		
		if((state1 == State.TRUE && polarity1 || state1 == State.FALSE && !polarity1) || 
		   (state2 == State.TRUE && polarity2 || state2 == State.FALSE && !polarity2)){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		String res = "{ ";
		for (Integer i : literals)
			res += i + " ";
		return res + "}" + ", sat = "+", unassigned = " + lit1 + "  " + lit2;
	}

}