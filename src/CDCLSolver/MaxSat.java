package CDCLSolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

import dataStructure.Clause;
import dataStructure.ClauseSet;
import dataStructure.Variable;

public class MaxSat {

	Integer lastHightestID = null;
	ClauseSet instance = null;
	
	public MaxSat(ClauseSet instance){
		this.instance = instance;
	}
	
	public int nonPartionFuMalik(){
		int cost = 0;
		while(true){
			System.out.println("\n\n");
			CDCL solver = new CDCL(instance);
			HashSet<Clause> unsatCore = solver.solve();
			if(unsatCore == null){
				return cost;
			}
	
			Vector<Integer> blockingVariables = new Vector<Integer>();
			
			for(Clause coreClause : unsatCore){	
				if(coreClause.isHard){
					continue;
				}
			
				int newID = getHighestID(solver.variables);
				instance.removeClause(coreClause);
				
				Vector<Integer> newLiterals = coreClause.getLiterals();
				newLiterals.add(newID);
				
				instance.addVariable(newID);
				Clause newClause = new Clause(newLiterals, coreClause.variables);
				instance.initNewClause(newClause);
				blockingVariables.add(newID);
			}
			if(blockingVariables.size() == 0){
				return 0;
			}
			instance.exactlyOneConstraint(blockingVariables);
			instance = instance.rebuildClauseSet();
			cost++;
		}
	}
			
	

	/**
	 * Sucht eine ID für die neue Blocking variable, höchster alter ID wert + 1;
	 * @param variables Variablen der Instanz
	 * @return Neue ID
	 */
	private int getHighestID(HashMap<Integer, Variable> variables) {
		int minID = Integer.MIN_VALUE;
		if(this.lastHightestID == null){
			for(Variable currentVariable : variables.values()) {
				if(currentVariable.getId() > minID){
					minID = currentVariable.getId();
				}

			}
			minID++;
			return minID;
		}
		else{
			minID++;
			return minID;
		}
	}


}
