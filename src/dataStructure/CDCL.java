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
					// Get all literals of c1 and c2, except for literalC1 and literalC2
					// but we only want no duplicate literals.
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
	
	protected Clause get1UIP(Clause conflict,  Clause reason) {
		return null;
	}
	
	protected int analyseConflict(Clause conflict) {
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
		for(Variable variable : variables) {
			if (variable.getActivity() > nextVariable.getActivity()) {
				nextVariable = variable;
			}
		}
		return nextVariable;
	}
}
