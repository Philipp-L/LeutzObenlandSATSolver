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
	
	/* The activity of this variable. s*/
	private float activity = 1.0f;
	
	/* Because of this clause, the variable was assigned to this value. Null, if this was a decision. */
	private Clause reason;
	
	/* The level, where this variable was assigned. */
	private int level;

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
	 * Construction - only for Test purposes
	 * @param id id of the Viariable
	 * @param initialState inital State of the Variable
	 */
	public Variable(int id, State initialState) {
		this.id = id;
		this.watched = new Vector<>();
		this.state = initialState;
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
		for (Clause currentClause : new Vector<>(watched)) {
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
		String res = "[" + state + ", activity: " + activity;
		res += "\n\tAdjacence List: " + watched;
		return res + "\n]";
	}

	public void isWatchedBy(Clause clause) {
		watched.add(clause);
	}
	
	public void removeWatchedBy(Clause clause){
		watched.remove(clause);
	}
	
	public void increaseActivity() {
		activity *= 1.1d;
	}
	
	public void decreaseActivity() {
		activity *= 0.95d;
	}
	public float getActivity() {
		return activity;
	}
	public Clause getReason() {
		return reason;
	}
	public void setReason(Clause reason) {
		this.reason = reason;
	}
}