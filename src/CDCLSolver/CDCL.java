package CDCLSolver;

import java.io.IOException;
import java.util.Collection;
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

	private final float INCREASE_FACTOR = (float)1.1;
	private final float DECREASE_FACTOR = (float)0.95;
	
	private int currentDecisionLevel;
	private final ClauseSet clauses;
	private final Stack<Variable> stack;
	private final HashMap<Integer, Variable> variables;
	private final Vector<Clause> units;
	private Clause lastLearned = null;
	
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
	 * Findet die 1UIP Clause durch abbauen des Stacks und resolvieren der reasons
	 * Hierbei werden die Originalen KLauseln der neuen Klausel in der KLuasel getracked
	 * @param conflict
	 *            Conflict Klausel für Empty Klausel
	 * @param reason
	 *            Grund für Zuweißung des Conflicts
	 * @return Neu gelernte Klausel
	 */
	public Clause get1UIP(Clause conflict, Clause reason) {
		Clause newClause = resolve(conflict, reason);
		newClause.addOrigClauses(conflict);
		newClause.addOrigClauses(reason);
		while (!is1UIP(newClause)) {
			final Variable cv = chooseLiteral(newClause);
			HashSet<Clause> origClauses = newClause.getOriginalClauses();
			newClause = resolve(newClause, cv.reason);
			newClause.addOrigClauses(origClauses);
			newClause.addOrigClauses(cv.reason);
			cv.unAssign(variables);
		}
		//System.out.println(newClause.getOriginalClauses());
		return newClause;
	}

	
	/**Überprüft ob eine gegebene Klausel die 1UIP ist
	 * Das kriterieum hierfür ist die Anzahl an Variablen, welche
	 * auf höchstem Decision Level assigned wurden, ist diese 1, ist die Klausel 1UIP
	 * 
	 * @param clauseToCheck Klausel für die überprüft wird, ob sie 1UIP ist
	 * @return ergebnis, ob die Klausel 1UIP war
	 */
	private boolean is1UIP(Clause clauseToCheck) {
		int numOfMaxLvl = 0;
		for (final int i : clauseToCheck.getLiterals()) {
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
	//	System.out.println("Learn: " + newClause);
		learnNewUnitClause(newClause);
		return computeBacktrackLevel(newClause);
	}

	
	/**
	 * Fügt eine gelernte Klausel der Klausemenge hinzu,
	 * 
	 * @param newClause neue unit klausel die gelernt wird
	 */
	private void learnNewUnitClause(Clause newClause) {
		increaseAcitvityOfVariables(newClause);
		clauses.addNewClause(newClause);
		units.addElement(newClause);
		lastLearned = newClause;
	}

	/**
	 * Erhöt die Aktivität aller Variablen einer gegebenen KLausel
	 * Wird aufgerufen, nachdem die KLausel gelernt wurde
	 * 
	 * @param newClause gegene Klausel
	 */
	private void increaseAcitvityOfVariables(Clause newClause) {
		Vector<Integer> literals = newClause.getLiterals();
		for(int currentLiteral : literals){
			variables.get(currentLiteral).computeAcitivity(INCREASE_FACTOR);
		}
	}

	/**
	 * Sucht die nächste Variable vom Stack für eine Resolution,
	 * Dies ist wichtig, falls sich durch Unitpropagation ein
	 * verzweigter Abhänigkeitsbaum ergebene hat, es muss für die Resolution von einem 
	 * Konflikt aus der Richtige Pfad zur 1UIP zurückverfolgt werden
	 * 
	 * @param newClause Klausel anhand dererer die 1UIP zurückverfolgt wird
	 * @return Nächste Variable deren Reason auf dem weg zur 1uip liegt
	 */
	private Variable chooseLiteral(Clause newClause) {
		while(true) {
			Variable nextVariableFromStack = stack.pop();
			for (final Integer i : newClause.getLiterals()) {
				if (Math.abs(i) == nextVariableFromStack.getId()) {
					return nextVariableFromStack;
				}
			}
			nextVariableFromStack.unAssign(variables);
		}
	}

	/**Gibt das Level zurück auf das gebacktrackt werden muss
	 * Hierbei handelt es sich um das 2. höchste Level, auf dem Variablen
	 * der Klausel zugewiesen wurden
	 * 
	 * @param newClause Klausel für die das Backtracklevel gesucht wird
	 * @return das Backtracklevel
	 */
	private int computeBacktrackLevel(Clause newClause) {
		int level = 0;
		for (final Integer literal : newClause.getLiterals()) {
			int variableLevel = variables.get(literal).getLevel();
			if (variableLevel > level && variableLevel != currentDecisionLevel) {
				level = variableLevel;
			}
		}
		//System.out.println("Backtrack level: " + level);
		return level;
	}

	/** Gibt die Nächste variable der Assigned werden soll zurück.
	 * Die auswahl geschieht aufgrund der Aktivität
	 * @return die aktivste Variable
	 */
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

	/**Löst die gegebene SAT instanz
	 * @return true wenn die Instannz lösbar ist, sonst false
	 */
	public HashSet<Clause> solve() {
		while (true) {
			//System.out.println();
			Clause emptyClause = this.clauses.unitPropagation(stack, currentDecisionLevel);
			if (emptyClause != null) {
				// After a conflict, we will do a back jump, so the units will
				// be invalid.
				this.units.clear();

				int returnedLevel = analyseConflict(emptyClause);
				if (returnedLevel == -1) {
					return lastLearned.getOriginalClauses();
				}
				backtrackToLevel(returnedLevel);
			} else if (clauses.allClausesAreSAT()) {
				return null;
			} else {
				this.currentDecisionLevel++;
				Variable nextVariable = getNextVar();
				if (nextVariable == null) {
					return lastLearned.getOriginalClauses();
				}
				//System.out.println("Decision: Assign next variable to false: " + nextVariable.getId());
				decreaseAcivityOfAllVariables();
				emptyClause = nextVariable.assign(false, null, variables, units, stack, currentDecisionLevel);
			}
		}
	}

	/**
	 * Senkt die Aktivität aller Variablen um den festgelegten Faktor
	 * Wird aufgerufen wenn eine neue Variable durch Decision gelernt wird
	 */
	private void decreaseAcivityOfAllVariables() {
		Collection<Variable> variableList = variables.values();
		for(Variable currentVariable : variableList){
			currentVariable.computeAcitivity(DECREASE_FACTOR);
		}
	}

	/** Löst alle bis zum gegebene level assigned variablen wieder aus dem Stack
	 * @param level level auf das gebacktracked wird
	 */
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
