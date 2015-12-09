package CDCLSolver;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import dataStructure.Clause;
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
					newClause.initWatch(variables);
					return newClause;
				}
			}
		}
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
	public Clause get1UIP(Clause conflict, Clause reason) {
		Clause newClause = resolve(conflict, reason);
		while (!is1UIP(newClause)) {
			final Variable cv = chooseLiteral(newClause);
			newClause = resolve(newClause, cv.reason);
			cv.unAssign(variables);
		}
		return newClause;
	}

	private boolean is1UIP(final Clause c) {
		int numOfMaxLvl = 0;
		for (final int i : c.getLiterals()) {
			if (variables.get(i).getLevel() == currentDecisionLevel)
				numOfMaxLvl++;
		}
		return (numOfMaxLvl <= 1);
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
		if (currentDecisionLevel == 0) {
			return -1;
		}
		Variable lv = stack.pop();
		Clause reason = lv.reason;
		lv.unAssign(variables);

		Clause newClause = get1UIP(conflict, reason);
		System.out.println("Learn: " + newClause);
		clauses.addNewClause(newClause);
		units.addElement(newClause);
		lastLearned = newClause;
		return computeBacktrackLevel(newClause);
	}
	Clause lastLearned = null;
	
	private Variable chooseLiteral(Clause newClause) {
		while(true) {
			Variable v = stack.pop();
			for (final Integer i : newClause.getLiterals()) {
				if (Math.abs(i) == v.getId()) {
					return v;
				}
			}
			v.unAssign(variables);
		}
	}

	private int computeBacktrackLevel(Clause newClause) {
		int level = 0;
		for (final Integer literal : newClause.getLiterals()) {
			int variableLevel = variables.get(literal).getLevel();
			if (variableLevel > level && variableLevel != currentDecisionLevel) {
				level = variableLevel;
			}
		}
		System.out.println("Backtrack level: " + level);
		return level;
	}

	private Variable getNextVar() {
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
			System.out.println();
			Clause emptyClause = this.clauses.unitPropagation(stack, currentDecisionLevel);
			if (emptyClause != null) {
				// After a conflict, we will do a back jump, so the units will
				// be invalid.
				this.units.clear();

				int returnedLevel = analyseConflict(emptyClause);
				if (returnedLevel == -1) {
					return false;
				}
				backtrackToLevel(returnedLevel);
			} else if (clauses.allClausesAreSAT()) {
				return true;
			} else {
				this.currentDecisionLevel++;
				Variable nextVariable = getNextVar();
				if (nextVariable == null) {
					return false;
				}
				System.out.println("Decision: Assign next variable to false: " + nextVariable.getId());
				emptyClause = nextVariable.assign(false, null, variables, units, stack, currentDecisionLevel);
			}
		}
	}

	private void backtrackToLevel(int level) {
		while (!stack.isEmpty() && stack.peek().getLevel() > level) {
			stack.pop().unAssign(variables);
		}
		this.currentDecisionLevel = level;
		lastLearned.reWatch(variables, 0);
		System.out.println("Last learned after backtracking: " + lastLearned);
	}

	private String stackToString() {
		String s = "";
		Collections.reverse(stack);
		for (Variable v : stack) {
			s += (v.getId() + "(" + v.getState() + ")"
					+ (v.reason == null ? "[d" + v.getLevel() + "]" : "[p" + v.getLevel() + "]") + ", ");
		}
		Collections.reverse(stack);
		return s;
	}
}
