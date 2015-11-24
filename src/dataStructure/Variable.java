package dataStructure;

import java.util.HashMap;
import java.util.Vector;

import dataStructure.Clause.ClauseState;

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
	private final int id;

	/* Clauses containing this variable */
	private Vector<Clause> watched;

	/**
	 * Creates a variable with the given ID.
	 * 
	 * @param id
	 
	*            ID of the variable
	*/
	public Variable(int id) {
		this.id = id;
		this.state = State.OPEN;
		this.watched = new Vector<>();
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
		return watched;
	}

	/**
	 * Assigns variable with the given value and updates the internal state of
	 * the corresponding clauses.
	 * 
	 * @param val
	 *            value to be assigned
	public void assign(boolean val) {
		if (this.state == State.OPEN) {
			for (Clause clause : watched) {
				clause.setNumUnassigned(clause.getNumUnassigned() - 1);
			}
		}
		this.state = val ? State.TRUE : State.FALSE;
		for (Clause clause : watched) {
			clause.checkSat();
		}
	}
	 */

	/**
	 * Belegt diese variable mit dem Wert val
	 * Suche in klauseln mit zu 0 evaluiertem Litral neue Watched literal, über rewatch
	 * Fügt unit clauses der Unit Liste hinzu
	 *  
	 * @param val Wert der der Variablen zugewiesen wird
	 * @param variables hashmap der variablen
	 * @param units Liste der Unit clauses
	 * @return Leere Klausel oder null, wenn keine existiert
	 */
	public Clause assign(boolean val, HashMap<Integer, Variable> variables,
			Vector<Clause> units){
		this.state = val ? State.TRUE : State.FALSE;
		
		for (Clause currentClause : watched) {
			ClauseState variableState = currentClause.reWatch(variables, this.id);
			if(variableState == ClauseState.EMPTY){
				return currentClause;
			}
			else if(variableState == ClauseState.UNIT){
				units.add(currentClause);
			}
		}
		return null;
	}

	
	@Override
	public String toString() {
		String res = "[" + state + " ";
		res += "\n\tAdjacence List: " + watched;
		return res + "\n]";
	}
}