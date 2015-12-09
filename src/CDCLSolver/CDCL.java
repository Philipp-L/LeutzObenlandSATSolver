package CDCLSolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import dataStructure.Clause;
import dataStructure.Clause.ClauseState;
import dataStructure.ClauseSet;
import dataStructure.Variable;
import dataStructure.Variable.State;

public class CDCL {

	public final ClauseSet clauses;
	public final Stack<Variable> stack;
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
		System.out.println(c1 + "  " + c2);
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
		Set<Integer> literals = new HashSet<>(c1.getLiterals().size() + c2.getLiterals().size());
		literals.addAll(c1.getLiterals());
		literals.addAll(c2.getLiterals());
		return new Clause(new Vector<>(literals), variables);
	}

	
	
	/**
	 * Findet die 1UIP Clause Backtrackt dabei durch den Stack - sollte
	 * wietermachen bsi Clause unit ist
	 * 
	 * @param conflict
	 *            Conflict Klausel f�r Empty Klausel
	 * @param reason
	 *            Grund f�r Zuwei�ung des Conflicts
	 * @return Neu gelernte Klausel
	 */
	public Clause get1UIP(Clause conflict, Clause reason) {
			if (reason == null) {
			currentDecisionLevel--;
			System.err.println("No reason!");
			//TODO Decrease
			return conflict;
		}

		Clause newClause = resolve(conflict, reason);

		int levelCounter = 0;

		//Wie viele Variablen sind auf h�chstem Decision Level?
		for (Integer i : newClause.getLiterals()) {
			System.out.println(variables.get(i).getLevel() +" " + currentDecisionLevel);
			if (variables.get(i).getLevel() >= currentDecisionLevel) {
				levelCounter++;
			}
		}
		//Es ist genau eine Variable auf h�chstem Decision Level!
		if (levelCounter <= 1) {
			newClause.initWatch(variables);
			System.out.println("learn Clause" + newClause);
			return newClause;
		}

		//Sollte eigentlich nie passieren!
		else if (stack.empty()) {
			newClause.initWatch(variables);
			return newClause;
		}

		return get1UIP(newClause, getNextVar().reason);
	}

	/**
	 * Erh�lt eine Conflict Klausel, welche den nun Empty ist Aus dem Variablen
	 * Stack wird die Letzte Variable ausgelesen, und deren Grund,
	 * 
	 * @param conflict
	 *            Conflict Klausel
	 * @return backtrack Level
	 */
	public int analyseConflict(Clause conflict) {
		Clause reason = getNextVar().reason;
		Clause newClause = conflict;
		newClause = get1UIP(newClause, reason);
		/**
		 * TODO: Idee:
		 * Wir haben eine neue Klausel zum lernen. Diese ist bis jetzt EMPTY,
		 * da alle Variablen belegt sind. Nun poppen wir so lange Variablen
		 * vom Stack und unassign sie, bis eine der Variablen in der neuen 
		 * Clausel enthalten ist. Dadurch bekommen wir eine Unit-Klausel.
		 * Dann poppen wir noch alle Variablen aus dem aktuellen Level,
		 * lernen die Klausel.

		Variable nextVar;
		while ((nextVar = getNextVar()) != null) {
			if (newClause.getLiterals().contains(nextVar.getId())
					|| newClause.getLiterals().contains(-nextVar.getId())) {
				System.out.println("Found first: " + nextVar.getId() + " with reason: " + nextVar.reason);
				while ((nextVar = getNextVar()) != null) {
					if (nextVar.reason == null) {
						clauses.initNewClause(newClause);
						System.out.println("Learned clausel: " + newClause);
						System.out.println("New level: " + stack.peek().getLevel() + " <=> " + currentDecisionLevel);
						return stack.peek().getLevel();
					}
				}
				return -1;
			}
		}
		return -1;

		 */

		//TODO - wir haben iene 1UIP - Solange Backtracken (stack poppen) wie die neue KLausel unit bleibt!
		//TODO - Neue KLausel der Klauselmenge hinzuf�gen, sie ist automatisch unit - unit propagation ausf�hren.

		newClause.initWatch(variables);
		while(newClause.reWatch(variables, 0) == ClauseState.EMPTY){
			Variable nextVar = getNextVar();
			if(nextVar.reason == null){
				currentDecisionLevel--;
			}
		}
		units.addElement(newClause);
		clauses.addNewClause(newClause);
		System.out.println("newClause is now" + newClause);
		return currentDecisionLevel;
	}

	private Variable getNextVar() {
		Variable currentVariable = stack.pop();
		currentVariable.unAssign();
		// TODO: decrease activity?
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
				this.currentDecisionLevel = analyseConflict(emptyClause);
				if (this.currentDecisionLevel == 0) {
					return false;
				}
			} else if (clauses.allClausesAreSAT()) {
				return true;
			} else {
				this.currentDecisionLevel++;
				Variable nextVariable = getHighestAcitivityVariable();
				System.out.println("Decision: Assign next variable to false: " + nextVariable.getId());
				emptyClause = nextVariable.assign(false, null, variables, units, stack, currentDecisionLevel);
				System.out.println("Empty clause from assignment: " + emptyClause);
			}
		}
	}

	private String stackToString() {
		String s = "";
		for (Variable v : stack) {
			s += (v.getId() + "("+v.getState()+")" + (v.reason == null ? "[d" + v.getLevel() + "]" : "[p" + v.getLevel() + "]") + ", ");
		}
		return s;
	}
}
