package dataStructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import dataStructure.Clause.ClauseState;
import parser.DimacsParser;

/**
 * A set of clauses.
 * 
 */
public class ClauseSet {

	private final float INCREASE_FACTOR = (float)1.1;
	private final float DECREASE_FACTOR = (float)0.95;

	/* Number of variables */
	private int varNum;
	/* Clauses of this set */

	private Vector<Clause> clauses = new Vector<>();

	/* Unit clauses of this list */
	public Vector<Clause> units;

	/* List of all variables */
	private HashMap<Integer, Variable> variables;

	/**
	 * Constructs a clause set from the given DIMACS file.
	 * 
	 * @param filePath
	 *            file path of the DIMACS file.
	 * @throws IOException
	 */
	public ClauseSet(String filePath) throws IOException {
		DimacsParser dimacsParser = new DimacsParser(filePath);
		this.variables = new HashMap<>(dimacsParser.getNumberOfVariables());
		this.varNum = dimacsParser.getNumberOfVariables();
		this.units = new Vector<>();

		Vector<Clause> clauses = new Vector<>(dimacsParser.getNumberOfClauses());

		for (Vector<Integer> rawClause : dimacsParser.getFormula()) {
			Clause clause = new Clause(rawClause, variables);
			clauses.add(clause);
			for (Integer literal : rawClause) {
				addVariable(literal);
			}
		}

		init(clauses.toArray(new Clause[clauses.size()]));
	}

	public void initAcitivy(){
		for(Integer entry : variables.keySet()){
			variables.get(entry).initAcitivty();
		}
	}

	public void addNewClause (Clause clause){
		clauses.addElement(clause);
	}
	
	private Variable addVariable(Integer literal) {
		Variable variable = variables.get(literal);
		if (variable == null) {
			int variableId = Math.abs(literal);
			variable = new Variable(variableId);
			variables.put(variableId, variable);
			variables.put(-variableId, variable);
		}
		return variable;
	}

	/**
	 * Creates a ClauseSet from a some Clauses. For testing.
	 * 
	 * @param clauses
	 */
	public ClauseSet(HashMap<Integer, Variable> variables, Clause... clauses) {
		this.variables = variables;
		this.clauses = new Vector<>(clauses.length);
		this.units = new Vector<>();
		this.varNum = variables.size() / 2;
		init(clauses);
	}

	private void init(Clause[] clauses) {
		for (Clause currentClause : clauses) {
			ClauseState currentState = currentClause.initWatch(variables);
			if (currentState == ClauseState.UNIT) {
				this.units.addElement(currentClause);
				this.clauses.addElement(currentClause);
			} else if (currentState == ClauseState.EMPTY) {
				continue;
			} else {
				this.clauses.addElement(currentClause);
			}
		}
	}

	/**
	 * Initialisiert eine neue Klausel
	 * @param newClause
	 */
	public void initNewClause(Clause currentClause){
		ClauseState currentState = currentClause.initWatch(variables);
		if (currentState == ClauseState.UNIT) {
			this.units.addElement(currentClause);
			this.clauses.addElement(currentClause);
		} else if (currentState == ClauseState.EMPTY) {
			throw new IllegalStateException();
		} else {
			this.clauses.addElement(currentClause);
		}
	}

	/**
	 * Executes unit propagation and checks for the existence of an empty
	 * clause.
	 * 
	 * @return null if no empty clause exists or the empty clause.
	 */
	public Clause unitPropagation(Stack<Variable> stack, int currentDecisionLevel) {
		if (units.size() == 0) {
			return null;
		}		

		Vector<Clause> newUnits = new Vector<Clause>();
		Vector<Clause> unitsCopie = new Vector<>();
		unitsCopie.addAll(units);
		for (Clause currentClause : unitsCopie) {
			if(currentClause.isSat()){
				units.remove(currentClause);	
				continue;
			}
			int currentUnassigned = currentClause.getUnassigned(variables);
			if (currentUnassigned == 0) {
				System.err.println("Komischer Fehler");
				System.exit(0);
				return currentClause;
			}
			boolean polarity = currentClause.getPolarity(currentUnassigned);
			Clause emptyClause = variables.get(currentUnassigned).assign(polarity, currentClause, variables, newUnits, stack, currentDecisionLevel);	
			System.out.println("Assign Variable By Propagation " + variables.get(currentUnassigned).getId() +"  " + polarity+ " Reason " + currentClause.getLiterals());
			units.remove(currentClause);	
			
			if (emptyClause != null) {
				System.out.println(emptyClause);
				return emptyClause;
			}
		}
		units.addAll(newUnits);
		if(newUnits.size() == 0){
			return null;
		}
		return unitPropagation(stack, currentDecisionLevel);
	}

	@Override
	public String toString() {
		return clausesToString() + "\n\n" + varsToString();
	}

	/**
	 * Returns all clauses as string representation.
	 * 
	 * @return a string representation of all clauses.
	 */
	public String clausesToString() {
		String res = "";
		for (Clause clause : clauses)
			res += clause + "\n";
		return res;
	}

	/**
	 * Returns all variables as string representation.
	 * 
	 * @return a string representation of all variables.
	 */
	public String varsToString() {
		String res = "";
		for (int i = 1; i <= varNum; i++)
			res += "Variable " + i + ": " + variables.get(i) + "\n\n";
		return res;
	}

	/**
	 * Erhöht die Aktivität einer variablen
	 */
	public void increaseAcitivty(){
		for(Integer entry : variables.keySet()){
			variables.get(entry).computeAcitivity(INCREASE_FACTOR);
		}
	}

	/**
	 * Senkt die Aktivität einer Variablen
	 */
	public void decreaseAcitivty(){
		for(Integer entry : variables.keySet()){
			variables.get(entry).computeAcitivity(DECREASE_FACTOR);
		}
	}

	//TODO Happens twice?
	public HashMap<Integer, Variable> getVariables(){
		return variables;
	}
	
	public boolean allClausesAreSAT(){
		for(Clause currentClause : clauses){
			if(!currentClause.isSat()){
				return false;
			}
		}
		return true;
	}
	

}