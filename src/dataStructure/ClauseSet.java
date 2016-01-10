package dataStructure;

import java.io.IOException;
import java.util.Collection;
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

	public void reset() {
		for (Variable variable : variables.values()) {
			variable.reset();
		}
		for (Clause clause : clauses) {
			clause.reset();
		}
		this.units.clear();
		init(clauses.toArray(new Clause[clauses.size()]));
	}
	
	public ClauseSet rebuildClauseSet(){
		ClauseSet newSet = new ClauseSet();
		
		for(Clause currentClause : this.clauses){
			Clause newClause = new Clause(currentClause.getLiterals(), newSet.variables);
			newSet.addNewClause(newClause);
			newSet.initNewClause(newClause);
		}
	
		for(Integer currentVariable : this.variables.keySet()){
			newSet.addVariable(currentVariable);
		}
		newSet.initAcitivy();
		return newSet;
	}
	
	public ClauseSet(){
		this.variables = new HashMap<Integer, Variable>();
		this.clauses = new Vector<Clause>();
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
	
	
	/**
	 * Initialisiert eine neue Klausel
	 * @param newClause neue KLausel
	 */
	public void initNewClause(Clause currentClause){
		ClauseState currentState = currentClause.initWatch(variables);
		if (currentState == ClauseState.UNIT) {
			this.units.addElement(currentClause);
			this.clauses.addElement(currentClause);
		} else {
			this.clauses.addElement(currentClause);
		}
	}
	public void initAcitivy(){
		for(Integer entry : variables.keySet()){
			variables.get(entry).initAcitivty();
		}
	}

	public void addNewClause (Clause clause){
		if (!clauses.contains(clause)) {
			clauses.addElement(clause);
		} else {
			//throw new IllegalStateException(clause.toString());
		}
	}

	public Variable addVariable(Integer literal) {
		Variable variable = variables.get(literal);
		if (variable == null) {
			int variableId = Math.abs(literal);
			variable = new Variable(variableId);
			variables.put(variableId, variable);
			variables.put(-variableId, variable);
		}
		return variable;
	}


	public void removeClause(Clause clause){
		this.clauses.remove(clause);
	}


	/**
	 * Initialisiert eine Menge von Klauseln
	 * @param clauses Klauselmenge
	 */
	private void init(Clause[] clauses) {
		for (Clause currentClause : clauses) {
			initNewClause(currentClause);
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
		for (Clause currentClause : new Vector<>(units)) {
			if(currentClause.isSat()){
				units.remove(currentClause);	
				continue;
			}
			int currentUnassigned = currentClause.getUnassigned(variables);
			if (currentUnassigned == 0) {
				units.remove(currentClause);
				continue;
			}
			boolean polarity = currentClause.getPolarity(currentUnassigned);
			Clause emptyClause = variables.get(currentUnassigned).assign(polarity, currentClause, variables, newUnits, stack, currentDecisionLevel);	
			//System.out.println("Assign Variable By Propagation " + variables.get(currentUnassigned).getId() +"  " + polarity+ " Reason " + currentClause.getLiterals());
			units.remove(currentClause);	

			if (emptyClause != null) {
				//System.out.println(emptyClause);
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
			res += clause + " |= " + clause.isSat() + "\n";
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

	/**
	 * Generiert eine Neue menger harter KLuaseln, die sicherstellt dass von den neuen Variablen höchstens eine wahr wird
	 * Bneutzt dafür eine Paarweise Codierung
	 * @param blockingVariables
	 */
	public void exactlyOneConstraint(Vector<Integer> blockingVariables) {
		for(int i = 0; i < blockingVariables.size(); i++){
			for(int j = i + 1; j < blockingVariables.size(); j++){
				Vector<Integer> newClauseLiterals = new Vector<Integer>();
				newClauseLiterals.add(blockingVariables.get(i)*-1);
				newClauseLiterals.add(blockingVariables.get(j)*-1);
				Clause currentClause = new Clause(newClauseLiterals, this.variables);
				this.initNewClause(currentClause);
				currentClause.isHard = true;
			}	
		}
		
		Clause lastNewClause = new Clause(blockingVariables, this.variables);
		this.initNewClause(lastNewClause);
		lastNewClause.isHard = true;
	}

	public Vector<Clause> getClauses() {
		return clauses;
	}
}


