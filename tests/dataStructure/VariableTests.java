package dataStructure;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import dataStructure.Clause.ClauseState;

public class VariableTests {
	
	
	HashMap<Integer, Variable> variables = new HashMap<>();
	Variable v1, v2, v3;
	Vector<Clause> units = new Vector<Clause>();
	Clause c1, c2, c3;
	
	@Before
	public void before(){
		v1 = new Variable(1);
		v2 = new Variable(2);
		v3 = new Variable(3);
		
		variables.clear();
		variables.put(1, v1);
		variables.put(-1, v1);
		variables.put(2, v2);
		variables.put(-2, v2);
		variables.put(3, v3);
		variables.put(-3, v3);
		units = new Vector<Clause>();
		c1 = new Clause(new Vector<Integer>(Arrays.asList(1)), variables);
		c2 = new Clause(new Vector<Integer>(Arrays.asList(-1,2)), variables);
		c3 = new Clause(new Vector<Integer>(Arrays.asList(-1,-2,-3)), variables);
	}
	
	@Test
	public void testId() {
		for (int i = 0; i < 100; i++) {
			int randomID = (int)(Math.random() * 1000);
			Variable variable = new Variable(randomID);
			assertEquals(randomID, variable.getId());
		}
	}

	@Test
	public void testState() {
		Variable variable = new Variable(1);
		assertEquals(Variable.State.OPEN, variable.getState());
		variable.assign(true, variables, units, new Stack<Variable>(), 0);
		assertEquals(Variable.State.TRUE, variable.getState());
		variable.assign(true, variables, units, new Stack<Variable>(), 0);
		assertEquals(Variable.State.TRUE, variable.getState());
		variable.assign(false, variables, units, new Stack<Variable>(), 0);
		assertEquals(Variable.State.FALSE, variable.getState());
		variable.assign(false, variables, units, new Stack<Variable>(), 0);
		assertEquals(Variable.State.FALSE, variable.getState());
		variable.assign(true, variables, units, new Stack<Variable>(), 0);
		assertEquals(Variable.State.TRUE, variable.getState());
	}

	@Test
	public void testAssignUnit() {
		assertEquals(c1.initWatch(variables), ClauseState.UNIT);
		assertEquals(c2.initWatch(variables), ClauseState.SUCCESS);
		assertEquals(c3.initWatch(variables), ClauseState.SUCCESS);
		units.add(c1);
		v1.assign(true, variables, units, new Stack<Variable>(), 0);
		assertEquals(c1.reWatch(variables, 1), ClauseState.SAT);
		assertEquals(c2.reWatch(variables, 1), ClauseState.UNIT);
		assertEquals(c3.reWatch(variables, 1), ClauseState.SUCCESS);
	}
	
	@Test
	public void testAssignEmpty() {
		assertEquals(c1.initWatch(variables), ClauseState.UNIT);
		assertEquals(c2.initWatch(variables), ClauseState.SUCCESS);
		assertEquals(c3.initWatch(variables), ClauseState.SUCCESS);
		units.add(c1);
		v1.assign(false, variables, units, new Stack<Variable>(), 0);
		assertEquals(c1.reWatch(variables, 1), ClauseState.EMPTY);
		assertEquals(c2.reWatch(variables, 1), ClauseState.SAT);
		assertEquals(c3.reWatch(variables, 1), ClauseState.SAT);
	}
	
	
}
