package CDCLSolver;

import java.io.IOException;
import java.nio.channels.NetworkChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.Vector;

import dataStructure.Clause;
import dataStructure.ClauseSet;
import dataStructure.Variable;
import dataStructure.Variable.State;

public class CDCL {

	public ClauseSet clauses;
	public Stack<Variable> stack;
	public int currentDecisionLevel;
	public HashMap<Integer, Variable> variables;
	
	public static void main(String[] args) throws IOException {
		CDCL instance = new CDCL(new ClauseSet("formula/formula01.cnf"), new Stack<Variable>());
		instance.solve();
	}

	public CDCL(ClauseSet instance, Stack<Variable> stack){
		instance.initAcitivy();
		this.clauses =instance;
		this.stack = stack;
		this.currentDecisionLevel = 0;
		this.variables = instance.getVariables();
	}
	
	public CDCL(ClauseSet instance, Stack<Variable> stack, HashMap<Integer, Variable> variables){
		this.clauses =instance;
		this.stack = stack;
		this.variables = variables;
	}
	
	/** Public for testing only
	 * Resolves two clauses and returns the resolvent
	 * @param c1 first clause to resolve
	 * @param c2 second clause to resolve
	 * @return resolved clause
	 */
	public Clause resolve(Clause c1, Clause c2){
		Vector<Integer> c1Variables = c1.getLiterals();
		Vector<Integer> c2Variables = c2.getLiterals();
		Vector<Integer> newVariables = new Vector<>();
		
		for(Integer i : c1Variables){
			if(c2Variables.contains(-i)){
				continue;
			}
			else{
				newVariables.add(i);
			}
		}
		
		for(Integer j : c2Variables){
			if(c1Variables.contains(-j) || newVariables.contains(j)){
				continue;
			}
			else{
				newVariables.add(j);
			}
		}
		
		return new Clause(newVariables, clauses.getVariables());
		
	} 
	
	/** Findet die 1UIP Clause
	 * Backtrackt dabei durch den Stack
	 * - sollte wietermachen bsi Clause unit ist
	 * @param conflict Conflict Klausel für Empty Klausel
	 * @param reason Grund für Zuweißung des Conflicts
	 * @return Neu gelernte Klausel
	 */
	public Clause get1UIP(Clause conflict, Clause reason){
		if(reason == null){
			return conflict;
		}
		
		
		Clause newClause = resolve(conflict, reason);
		int levelCounter = 0;
				
		for(Integer i : newClause.getLiterals()){
			if(variables.get(i).getLevel() >= currentDecisionLevel){
				levelCounter++;
			}
		}
		if(levelCounter <= 1){
			return newClause;
		}
		
		else
			if(stack.empty()){
				return newClause;
			}
			return get1UIP(newClause, getNextVar().reason);
	}
	
	
	/**
	 * Erhält eine Conflict Klausel, welche den nun Empty ist
	 * Aus dem Variablen Stack wird die Letzte Variable ausgelesen, und deren Grund,
	 * 
	 * @param conflict Conflict Klausel
	 * @return backtrack Level
	 */
	public int analyseConflict(Clause conflict){

		Clause reason = getNextVar().reason;
		Clause newClause = get1UIP(conflict, reason);
		this.clauses.initNewClause(newClause);

		this.currentDecisionLevel = stack.peek().getLevel();
		return currentDecisionLevel;
	}
	
	private Variable getNextVar(){
		Variable currentVariable = stack.pop();
		currentVariable.unAssign();
		return currentVariable;
	}
	
	private Variable getHighestAcitivityVariable(){
		float maxAcivity = Float.MIN_VALUE;
		Variable currentMaxVariable = null;
		for(Entry<Integer, Variable> currentEntry : variables.entrySet()){
			if(!(currentEntry.getValue().getState() == State.OPEN)){
				continue;
			}
			float currentAcivity = currentEntry.getValue().getAcivity();
			if(currentAcivity > maxAcivity){
				maxAcivity = currentAcivity;
				currentMaxVariable = currentEntry.getValue();
			}
		}
		return currentMaxVariable;
	}
	
	public boolean solve(){
		while(!clauses.allClausesAreSAT()){
			System.out.println(getHighestAcitivityVariable().getId());
			System.exit(0);
		}
		return false;
	}
}
