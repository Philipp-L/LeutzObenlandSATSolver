package CDCLSolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.swing.plaf.synth.SynthSpinnerUI;

import dataStructure.Clause;
import dataStructure.Clause.ClauseState;
import dataStructure.ClauseSet;
import dataStructure.Variable;
import dataStructure.Variable.State;

public class CDCL {

	public final ClauseSet clauses;
	private final Stack<Variable> stack;
	public int currentDecisionLevel;
	public final HashMap<Integer, Variable> variables;
	public final Vector<Clause> units;

	public static void main(String[] args) throws IOException {
		CDCL instance = new CDCL(new ClauseSet("formula/formula02.cnf"));
		instance.solve();
	}

	public CDCL(ClauseSet instance) {
		this(instance, new Stack<Variable>(), instance.getVariables());
	}

	public CDCL(ClauseSet instance, Stack<Variable> stack, HashMap<Integer, Variable> variables) {
		this.clauses = instance;
		this.stack = stack;
		this.variables = variables;
		this.units = instance.units;
		this.currentDecisionLevel = 0;
		instance.initAcitivy();
	}

	/**
	 * Public for testing only Resolves two clauses and returns the resolvent
	 * 
	 * @param c1
	 *            first clause to resolve
	 * @param c2
	 *            second clause to resolve
	 * @return resolved clause
	 */
	public Clause resolve2(Clause c1, Clause c2) {
		Vector<Integer> c1Variables = c1.getLiterals();
		Vector<Integer> c2Variables = c2.getLiterals();
		Vector<Integer> newVariables = new Vector<>();

		for (Integer i : c1Variables) {
			if (c2Variables.contains(-i)) {
				continue;
			} else {
				newVariables.add(i);
			}
		}

		for (Integer j : c2Variables) {
			if (c1Variables.contains(-j) || newVariables.contains(j)) {
				continue;
			} else {
				newVariables.add(j);
			}
		}

		return new Clause(newVariables, clauses.getVariables());

	}

	protected Clause resolve(Clause c1, Clause c2) {
		for (int literalC1 : c1.getLiterals()) {
			for (int literalC2 : c2.getLiterals()) {
				if (literalC1 == -literalC2) {
					// Get all literals of c1 and c2, except for literalC1 and
					// literalC2, but we only want no duplicate literals.
					Set<Integer> literals = new HashSet<>(c1.getLiterals().size() + c2.getLiterals().size());
					literals.addAll(c1.getLiterals());
					literals.addAll(c2.getLiterals());
					literals.remove(literalC1);
					literals.remove(literalC2);
					Clause newClause = new Clause(new Vector<>(literals), variables);
					return newClause;
				}
			}
		}
		System.out.println("Die Clause und Reason haben sich kein Literal geteilt, sie waren unabhänig! - Clause resolve");
		System.out.println(c1.getLiterals());
		System.out.println(c2.getLiterals());
		System.out.println(variables.get(c1.getLiterals().get(1)).getLevel());
		System.out.println(currentDecisionLevel);
		return null;
	}



	/**
	 * Findet die 1UIP Clause Backtrackt dabei durch den Stack - sollte
	 * wietermachen bsi Clause unit ist
	 * 
	 * @param conflict
	 *            Conflict Klausel für Empty Klausel
	 * @param reason
	 *            Grund für Zuweißung des Conflicts
	 * @return Neu gelernte Klausel
	 */
	public Clause get1UIP(Clause conflict, Variable nextVar) {
		Clause reason = nextVar.reason;
		if (reason == null) {				
			System.out.println(conflict);
			System.out.println("Das hätte nicht passieren dürfen - Vorher KANN nurnoch eine Variable auf höchsten Level gewesen sien");
			System.out.println(nextVar.getId());
			System.out.println(conflict.getLiterals());
			System.exit(0);
		}

		Clause newClause = resolve(conflict, reason);
		//Es gab einen Branch im Implikationsgraph - wir gehen weiter die Tabelle durch,
		//bis side die branches wieder treffen
		if(newClause == null){
			nextVar.unAssign();
			return get1UIP(conflict, getNextVar());
		}
		System.out.println(conflict + "   " + reason);
		int levelCounter = 0;

		//Wie viele Variablen sind auf höchstem Decision Level?
		for (Integer i : newClause.getLiterals()) {
			//System.out.println(variables.get(i).getLevel() +" " + currentDecisionLevel + " " + i);
			if (variables.get(i).getLevel() == currentDecisionLevel) {
				levelCounter++;
			}

			if(variables.get(i).getLevel() > currentDecisionLevel){
				
				System.err.println("Das sollte nicht passieren - decision level der variable kann nicht übermaximal sein");
				System.out.println(variables.get(i).getId() +"  " + variables.get(i).getLevel() + "  " + currentDecisionLevel);
				System.exit(0);
			}
		}
		//Es ist genau eine Variable auf höchstem Decision Level!
		if (levelCounter == 1) {
			newClause.initWatch(variables);
			System.out.println("learn Clause" + newClause);
			nextVar.unAssign();
			return newClause;
		}
		
		else if(levelCounter == 0){
			System.err.println(newClause + " Hier is was schief glaufen - keine Variable auf höchstem level - woher kam die prpagation? LEVLE: " + currentDecisionLevel);
			System.exit(0);
		}

		//Sollte eigentlich nie passieren!
		else if (stack.empty()) {
			System.err.println("Wir haben den Stack leer gemacht - das kann nicht passieren! Vorher kann nurnoch eine Variable auf höchstem Level  gewesen sein");
			newClause.initWatch(variables);
			nextVar.unAssign();
			return newClause;
		}
		nextVar.unAssign();
		return get1UIP(newClause, getNextVar());
	}

	/**
	 * Erhält eine Conflict Klausel, welche den nun Empty ist Aus dem Variablen
	 * Stack wird die Letzte Variable ausgelesen, und deren Grund,
	 * 
	 * @param conflict
	 *            Conflict Klausel
	 * @return backtrack Level
	 */
	public int analyseConflict(Clause conflict) {
		Clause reason = stack.peek().reason;
		if(reason == null){
			System.err.println("Conflict Hätte Unit sein müssen!");
			System.exit(0);
		}

		Clause newClause = conflict;
		newClause = get1UIP(newClause, getNextVar());
		newClause.initWatch(variables);

		if(newClause.reWatch(variables, 0) != ClauseState.EMPTY){
			System.err.println("Neue Variable MUSS Empty sein, sonst haben wir zu lang resovled!!");
			System.exit(0);
		}

		while(newClause.reWatch(variables, 0) == ClauseState.EMPTY){
			getNextVar().unAssign();
		}
		while(newClause.reWatch(variables, 0) == ClauseState.UNIT){
			Variable nextVar = getNextVar();
			Clause oldReason = nextVar.reason;
			State oldState = nextVar.getState();
			int oldDecisionLevel = nextVar.getLevel(); 
			
			boolean assignment = true;
			if(oldState == State.FALSE){
				assignment = false;
			}
			
			if(nextVar.reason == null){
				currentDecisionLevel--;
			}
			nextVar.unAssign();
			if(newClause.reWatch(variables, 0) != ClauseState.UNIT){
				nextVar.assign(assignment, oldReason, variables, units, stack, oldDecisionLevel);
				stack.push(nextVar);
				break;
			}
			else{
				nextVar.unAssign();
			}
		}

		if(newClause.reWatch(variables, 0) != ClauseState.UNIT){
			System.err.println("JETZT muss sie unit sein, sonst ging was schief!");
			System.exit(0);
		}

		units.addElement(newClause);
		clauses.addNewClause(newClause);
		return currentDecisionLevel;
	}

	private Variable getNextVar() {
		if(stack.empty()){
			System.out.println("Should not happen");
			System.exit(0);
		}
		Variable currentVariable = stack.pop();
		System.out.println("POP " + currentVariable.getId());
		//currentVariable.unAssign();
		// TODO: unassign variable when needed!
		return currentVariable;
	}

	private Variable getHighestAcitivityVariable() {
		float maxAcivity = Float.NEGATIVE_INFINITY;
		Variable currentMaxVariable = null;
		for (Variable currentVariable : variables.values()) {
			if (!(currentVariable.getState() == State.OPEN)) {
				continue;
			}
			float currentAcivity = currentVariable.getAcivity();
			if (currentAcivity > maxAcivity) {
				maxAcivity = currentAcivity;
				currentMaxVariable = currentVariable;
			}
		}
		return currentMaxVariable;
	}

	public boolean solve() {
		while (true) {
			Clause emptyClause = this.clauses.unitPropagation(stack, currentDecisionLevel);
			System.out.println("Unit propagation -> empty clause: " + emptyClause);
			if (emptyClause != null) {
				int returnedLevel = analyseConflict(emptyClause);
				if (returnedLevel == 0) {
					return false;
				}
			} else if (clauses.allClausesAreSAT()) {
				return true;
			} else {
				this.currentDecisionLevel++;
				Variable nextVariable = getHighestAcitivityVariable();
				System.out.println("Decision: Assign next variable to false: " + nextVariable.getId());
				emptyClause = nextVariable.assign(false, null, variables, units, stack, currentDecisionLevel);
			}
		}
	}

	private String stackToString() {
		String s = "";
		for (Variable v : stack) {
			s += (v.getId() + "("+v.getState()+")" + (v.reason == null ? "[d" + v.getLevel() + "]" : "[p" + v.getLevel() + "]") + ", \n");
		}
		return s;
	}
}
