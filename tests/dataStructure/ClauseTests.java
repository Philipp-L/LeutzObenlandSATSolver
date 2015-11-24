package dataStructure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

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
	
}
