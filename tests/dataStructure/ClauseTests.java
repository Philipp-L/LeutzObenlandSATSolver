package dataStructure;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class ClauseTests {
	Variable v1, v2, v3;
	Clause c1, c2;
	HashMap<Integer, Variable> variables = new HashMap<>();

	@Before
	public void before() {
		v1 = new Variable(1);
		v2 = new Variable(2);
		v3 = new Variable(3);

		variables.clear();
		variables.put(1, v1);
		variables.put(2, v2);
		variables.put(3, v3);
		c1 = new Clause(new Vector<>(Arrays.asList(1, 2)), variables);
		c2 = new Clause(new Vector<>(Arrays.asList(2, 3)), variables);
	}

	@Test
	public void testGetUnassigned() {
		int literal = c1.getUnassigned(variables);
		assertEquals(1, literal);

		v1.assign(true);
		v2.assign(true);

		literal = c1.getUnassigned(variables);
		assertEquals(0, literal);
	}

	@Test
	public void testCheckSet() {
		assertFalse(c1.isSat());
		c1.checkSat();
		assertFalse(c1.isSat());

		v1.assign(false);
		c1.checkSat();
		assertFalse(c1.isSat());

		v2.assign(false);
		c1.checkSat();
		assertFalse(c1.isSat());

		v1.assign(true);
		c1.checkSat();
		assertTrue(c1.isSat());

		v2.assign(true);
		c1.checkSat();
		assertTrue(c1.isSat());
	}

	@Test
	public void testIsEmpty() {
		assertTrue(new Clause(new Vector<Integer>(), variables).isEmpty());
		assertFalse(c1.isEmpty());
		v1.assign(false);
		assertFalse(c1.isEmpty());
		v2.assign(false);
		assertTrue(c1.isEmpty());
	}

	@Test
	public void testGetPolarity() {
		assertTrue(c1.getPolarity(1));
		assertTrue(c1.getPolarity(2));

		assertFalse(new Clause(new Vector<>(Arrays.asList(-1)), variables).getPolarity(0));
	}
	

	@Test
	public void testIsUnit() {
		assertFalse(c1.isUnit());
		v1.assign(true);
		assertTrue(c1.isUnit());
		v1.assign(false);
		assertTrue(c1.isUnit());
		v2.assign(false);
		assertFalse(c1.isUnit());
	}

}
