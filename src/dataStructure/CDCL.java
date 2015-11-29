package dataStructure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

public class CDCL {
	private final ClauseSet instance;
	private final Stack<Variable> stack = new Stack<>();

	public CDCL(ClauseSet instance) {
		this.instance = instance;
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
					return new Clause(new Vector<>(literals), c1.variables);
				}
			}
		}
		return null;
	}

	protected Clause get1UIP(Clause conflict, Clause reason) {
		// TODO:
		return null;
	}

	protected int analyseConflict(Clause conflict) {
		Variable variable = stack.pop();
		Clause newClause = resolve(conflict, variable.getReason());
		while (!isStopCiterionMet(newClause)) {
			Integer literal = chooseLiteral(newClause);
			Clause reason = instance.variables.get(literal).getReason();
			newClause = resolve(newClause, reason);
		}
		return computeBacktrackLevel(newClause);
	}

	private Integer chooseLiteral(Clause newClause) {
		// TODO:
		return newClause.getLiterals().get(0);
	}

	private boolean isStopCiterionMet(Clause newClause) {
		// TODO:
		return false;
	}

	private int computeBacktrackLevel(Clause newClause) {
		// TODO:
		return 0;
	}

	/**
	 * @return the variable which has the highest activity.
	 */
	protected Variable getNextVar() {
		Collection<Variable> variables = instance.variables.values();
		if (variables.isEmpty()) {
			return null;
		}
		Variable nextVariable = variables.iterator().next();
		for (Variable variable : variables) {
			if (variable.getActivity() > nextVariable.getActivity()) {
				nextVariable = variable;
			}
		}
		return nextVariable;
	}

	public boolean solve() {
		// TODO:
		int level = 0;
		while (true) {
			Clause emptyClause = instance.unitPropagation();
			if (emptyClause != null) {
				level = analyseConflict(emptyClause);
				if (level == -1) {
					return false;
				} //
				backtrace(level);
			} else { // TODO: is sat?
				++level;
				Variable variable = getNextVar();
			}
		}
	}

	private void backtrace(int level) {
		// TODO
	}
}
