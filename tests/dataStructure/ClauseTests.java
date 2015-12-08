package dataStructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import dataStructure.Clause.ClauseState;
import dataStructure.Variable.State;

public class ClauseTests {
	Variable v1, v2, v3;
	Clause c1, c2;
	HashMap<Integer, Variable> variables = new HashMap<>();
	Vector<Clause> units;
	@Before
	public void before() {
		units = new Vector<Clause>();
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
		c1 = new Clause(new Vector<>(Arrays.asList(1, 2)), variables);
		c2 = new Clause(new Vector<>(Arrays.asList(2, 3)), variables);
	}	
	
	@Test
	public void testGetPolarity() {
		assertTrue(c1.getPolarity(1));
		assertTrue(c1.getPolarity(2));

		assertFalse(new Clause(new Vector<>(Arrays.asList(-1)), variables).getPolarity(0));
	}
	
	@Test 
	public void testInitWatchEmpty(){
		Clause c3 = new Clause(new Vector<Integer>(), variables);
		assertTrue(c3.initWatch(variables) == ClauseState.EMPTY);
	}
	
	
	@Test 
	public void testInitWatchSucess(){
		c1 = new Clause(new Vector<>(Arrays.asList(1,2)), variables);
		assertEquals(ClauseState.SUCCESS, c1.initWatch(variables));
	}
	
	@Test 
	public void testInitWatchUnit(){
		c1 = new Clause(new Vector<>(Arrays.asList(1)), variables);
		assertTrue(c1.initWatch(variables) == ClauseState.UNIT);
	}
	
	
	@Test 
	public void testRewatchSucess(){
		v1 = new Variable(1, State.TRUE);
		variables.put(1, v1);
		variables.put(-1, v1);
		variables.put(2, v2);
		variables.put(-2, v2);
		variables.put(3, v3);
		variables.put(-3, v3);
		c1 = new Clause(new Vector<>(Arrays.asList(-1,2,3)), variables);
		c1.initWatch(variables);
		v1.assign(true, variables, units, new Stack<Variable>(), 0);
		assertEquals(ClauseState.SUCCESS, c1.reWatch(variables, 1));
		assertEquals(2, c1.lit1);
	}
	
	@Test 
	public void testRewatchUnit(){
		v1 = new Variable(1, State.TRUE);
		variables.put(1, v1);
		variables.put(-1, v1);
		variables.put(2, v2);
		variables.put(-2, v2);
		variables.put(3, v3);
		variables.put(-3, v3);
				
		c1 = new Clause(new Vector<>(Arrays.asList(-1,-2)), variables);
		c1.initWatch(variables);
		v1.assign(true, variables, units, new Stack<Variable>(), 0);
		assertTrue(c1.reWatch(variables, 1) == ClauseState.UNIT);
	}
	
	@Test 
	public void testRewatchSAT(){
		v1 = new Variable(1, State.TRUE);
		variables.put(1, v1);
		variables.put(-1, v1);
		variables.put(2, v2);
		variables.put(-2, v2);
		variables.put(3, v3);
		variables.put(-3, v3);		
		
		c1 = new Clause(new Vector<>(Arrays.asList(1)), variables);
		c1.initWatch(variables);
		v1.assign(true, variables, units, new Stack<Variable>(), 0);
		assertTrue(c1.reWatch(variables, 1) == ClauseState.SAT);
	}


	@Test 
	public void testRewatchEmpty(){
		v1 = new Variable(1, State.TRUE);
		variables.put(1, v1);
		variables.put(-1, v1);
		variables.put(2, v2);
		variables.put(-2, v2);
		variables.put(3, v3);
		variables.put(-3, v3);		
		
		c1 = new Clause(new Vector<>(Arrays.asList(1)), variables);
		c1.initWatch(variables);
		v1.assign(false, variables, units, new Stack<Variable>(), 0);
		assertTrue(c1.reWatch(variables, 1) == ClauseState.EMPTY);
	}
	
	@Test
	public void TestisCorrectlyAssigned(){
		v1 = new Variable(1);
		v2 = new Variable(2);
		v3 = new Variable(3);
		variables.put(1, v1);
		variables.put(-1, v1);
		variables.put(2, v2);
		variables.put(-2, v2);
		variables.put(3, v3);
		variables.put(-3, v3);		
		
		c1.initWatch(variables);
		v1.assign(true, variables, units, new Stack<Variable>(), 0);
		v2.assign(true, variables, units, new Stack<Variable>(), 0);
		
		c1 = new Clause(new Vector<>(Arrays.asList(1,-2,3)), variables);
		assertTrue(c1.evaulatesToTrue(1));
		assertFalse(c1.evaulatesToTrue(-2));
		assertFalse(c1.evaulatesToTrue(3));
	}
}
