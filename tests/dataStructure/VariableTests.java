package dataStructure;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class VariableTests {
	
	
	HashMap<Integer, Variable> variables;
	Variable v1, v2;
	Vector<Clause> units = new Vector<Clause>();
	@Before
	public void before(){
		v1 = new Variable(1);
		v2 = new Variable(2);

		variables.clear();
		variables.put(1, v1);
		variables.put(2, v2);

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
		variable.assign(true, variables, units);
		assertEquals(Variable.State.TRUE, variable.getState());
		variable.assign(true, variables, units);
		assertEquals(Variable.State.TRUE, variable.getState());
		variable.assign(false, variables, units);
		assertEquals(Variable.State.FALSE, variable.getState());
		variable.assign(false, variables, units);
		assertEquals(Variable.State.FALSE, variable.getState());
		variable.assign(true, variables, units);
		assertEquals(Variable.State.TRUE, variable.getState());
	}

	@Test
	public void testAssign() {
		//TODO
		Variable v1 = new Variable(1);
		Variable v2 = new Variable(2);
		Variable v3 = new Variable(3);

		HashMap<Integer, Variable> variables = new HashMap<>();
		variables.put(1, v1);
		variables.put(2, v2);
		variables.put(3, v3);
		Clause c1 = new Clause(new Vector<>(Arrays.asList(1,2)), variables);
		Clause c2 = new Clause(new Vector<>(Arrays.asList(2,3)), variables);

		v1.getAdjacencyList().add(c1);
		v2.getAdjacencyList().add(c1);
		v2.getAdjacencyList().add(c2);
		v3.getAdjacencyList().add(c2);
		
		assertEquals(2, c1.getNumUnassigned());
		assertEquals(2, c2.getNumUnassigned());
	/*	
		v1.assign(true);
		
		assertEquals(1, c1.getNumUnassigned());
		assertEquals(2, c2.getNumUnassigned());
		
		v2.assign(true);
		
		assertEquals(0, c1.getNumUnassigned());
		assertEquals(1, c2.getNumUnassigned());
		
		v2.assign(false);
		
		assertEquals(0, c1.getNumUnassigned());
		assertEquals(1, c2.getNumUnassigned());

		v3.assign(true);
		
		assertEquals(0, c1.getNumUnassigned());
		assertEquals(0, c2.getNumUnassigned());
*/
	}
	
	@Test
	public void testToString() {
		Variable v1 = new Variable(1);
		Variable v2 = new Variable(2);
		Variable v3 = new Variable(3);

		HashMap<Integer, Variable> variables = new HashMap<>();
		variables.put(1, v1);
		variables.put(2, v2);
		variables.put(3, v3);
		Clause c1 = new Clause(new Vector<>(Arrays.asList(1,2)), variables);
		Clause c2 = new Clause(new Vector<>(Arrays.asList(2,3)), variables);

		v1.getAdjacencyList().add(c1);
		v2.getAdjacencyList().add(c1);
		v2.getAdjacencyList().add(c2);
		v3.getAdjacencyList().add(c2);
		
		assertEquals("[OPEN \n	Adjacence List: [{ 1 2 }, sat = false, unassigned = 2]\n]", v1.toString());
	}
}
