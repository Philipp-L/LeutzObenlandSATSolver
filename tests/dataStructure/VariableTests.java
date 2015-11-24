package dataStructure;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class VariableTests {
	
	
	HashMap<Integer, Variable> variables = new HashMap<>();
	Variable v1, v2;
	Vector<Clause> units = new Vector<Clause>();
	@Before
	public void before(){
		v1 = new Variable(1);
		v2 = new Variable(2);

		variables.clear();
		variables.put(1, v1);
		variables.put(-1, v1);
		variables.put(2, v2);
		variables.put(-2, v2);

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
	}
}
