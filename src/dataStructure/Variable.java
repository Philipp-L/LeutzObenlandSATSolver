package dataStructure;

import java.util.HashMap;
import java.util.Stack;
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

	/* aktivität der variable */
	private float activity;
	
	/*Grund für belegung*/
	public Clause reason;
	/*level der belegung*/
	int level;
	
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
	 * Suche in klauseln mit zu 0 evaluiertem Litral neue Watched literal, Ã¼ber rewatch
	 * FÃ¼gt unit clauses der Unit Liste hinzu
	 *  
	 * @param val Wert der der Variablen zugewiesen wird
	 * @param variables hashmap der variablen
	 * @param units Liste der Unit clauses
	 * @return Leere Klausel oder null, wenn keine existiert
	 */
	public Clause assign(boolean val, Clause reason, HashMap<Integer, Variable> variables,
			Vector<Clause> units, Stack<Variable> stack, int currentDecisionLevel){		
		this.state = val ? State.TRUE : State.FALSE;
		this.level = currentDecisionLevel;
		this.reason = reason;
		stack.push(this);
		
		Vector<Clause> watchedCopie = new Vector<>();
		watchedCopie.addAll(watched);
		
		for (Clause currentClause : watchedCopie) {
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
		String res = getId() + "[" + state + " ";
		res += "Adjacence List: " + watched;
		return res + "]";
	}

	public void isWatchedBy(Clause clause) {
		watched.add(clause);
	}
	
	public void removeWatchedBy(Clause clause){
		watched.remove(clause);
	}
	
	public void computeAcitivity(float factor){
		this.activity = this.activity*factor;
	}
	
	/**
	 * Watched enthält alle Klauseln in dennen diese Variable vorkommt
	 * - entsprechend ist die initiale Aktivität gleich der Anzahl dieser Klauseln
	 */
	public void initAcitivty(){
		this.activity = this.watched.size();
	}
	
	public int getLevel(){
		return level;
	}
	
	/**
	 * Used for testing
	 */
	public void setReason(Clause reason){
		this.reason = reason;
	}
	
	public void setLevel(int level){
		this.level = level;
	}

	public void unAssign() {
		this.state = State.OPEN;
	}
	
	public float getAcivity(){
		return activity;
	}
}