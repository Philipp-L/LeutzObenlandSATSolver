package dataStructure;

import java.util.HashMap;
import java.util.Vector;

import dataStructure.Clause.ClauseState;
import dataStructure.Variable.State;

/**
 * A clause.
 * 
 */
public class Clause {
	/* Literals of the clause */
	private Vector<Integer> literals;

	public enum ClauseState {SAT, EMPTY, UNIT, SUCCESS};

	/* 2 Beobachtete Literale*/
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
	 * Returns an unassigned Literal, if one of the 2 watched is
	 * unassigned.
	 * 
	 * @return number of the unassigned literal
	 */
	public int getNumUnassigned() {
		if(variables.get(lit1).getState() == State.OPEN){
			return lit1;
		}
		else if (variables.get(lit1).getState() == State.OPEN) {
			return lit2;
		}
		return 0;
	}

	/**
	 * Returns an unassigned literal of this clause.
	 * 
	 * @param variables
	 *            variable objects
	 * @return an unassigned literal, if one exists, 0 otherwise
	 */
	/*
	public int getUnassigned(HashMap<Integer, Variable> variables) {
		for (Integer literal : literals) {
			if (variables.get(Math.abs(literal)).getState() == State.OPEN) {
				return literal;
			}
		}
		return 0;
	}
	*/

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
	public ClauseState initWatch(HashMap<Integer, Variable> variables){
		int numberOfLiterals = literals.size();
		if(numberOfLiterals == 0){
			return ClauseState.EMPTY;
		}
		else if(numberOfLiterals == 1){
			return ClauseState.UNIT;
		}

		else{
			lit1 = literals.get(0);
			lit2 = literals.get(1);
			return ClauseState.SUCCESS;
		}

	}

	public ClauseState reWatch(HashMap<Integer, Variable> variables, int id) {
		//Swap to be replaced literal to lit1
		if(id != lit1){
			lit1 = lit1 ^ lit2;
			lit2 = lit1 ^ lit2;
			lit1 = lit1 ^ lit2;
		}

		//find new watched literal
		for(int currentLiteral : literals){
			if((currentLiteral == lit1) || (currentLiteral == lit2)){
				continue;
			}

			State currentVariableState = variables.get(currentLiteral).getState();
			boolean currentPolarity = getPolarity(id);

			if((currentVariableState == State.OPEN) ||
				(currentVariableState == State.FALSE && !currentPolarity) ||
				(currentVariableState == State.TRUE && currentPolarity)){
				lit1 = id;
				return ClauseState.SUCCESS;
			}
			
		}

		//kein neues watched Literal gefunden
		boolean lit2Polarity = getPolarity(lit2);
		State lit2State = variables.get(lit2).getState();
		if((lit2State == State.FALSE && lit2Polarity) ||
				(lit2State == State.TRUE && !lit2Polarity)){
			return ClauseState.EMPTY;
		}
		if((lit2State == State.FALSE && !lit2Polarity) ||
				(lit2State == State.TRUE && lit2Polarity)){
			return ClauseState.SAT;
		}
		
		if(lit2State == State.OPEN){
			return ClauseState.UNIT;
		}
		
		return null;
	}

	@Override
	public String toString() {
		String res = "{ ";
		for (Integer i : literals)
			res += i + " ";
		return res + "}" + ", sat = " + ", unassigned = " ;
	}


}