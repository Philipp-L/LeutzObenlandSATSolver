package dataStructure;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class ClauseSetTests {
	Variable v1, v2;
	Clause c1, c2, c3;
	HashMap<Integer, Variable> variables = new HashMap<>();

	@Before
	public void before() {
		v1 = new Variable(1);
		v2 = new Variable(2);

		variables.clear();
		variables.put(1, v1);
		variables.put(2, v2);

		c1 = new Clause(new Vector<>(Arrays.asList(1)), variables);
		c2 = new Clause(new Vector<>(Arrays.asList(-1, 2)), variables);
		c3 = new Clause(new Vector<>(Arrays.asList(-1, -2)), variables);

		v1.getAdjacencyList().add(c1);
		v1.getAdjacencyList().add(c2);
		v1.getAdjacencyList().add(c3);
		v2.getAdjacencyList().add(c1);
		v2.getAdjacencyList().add(c2);
		v2.getAdjacencyList().add(c3);
	}

	@Test
	public void testUnitPropergation_Success() {
		ClauseSet set = new ClauseSet(variables, c1, c2);
		assertFalse(set.unitPropagation());

		System.out.println(set);
	}

	@Test
	public void testUnitPropergation_Failure() {
		ClauseSet set = new ClauseSet(variables, c1, c2, c3);
		assertTrue(set.unitPropagation());

		System.out.println(set);
	}

	@Test
	public void testUnitPropergation_Formula01() throws IOException {
		ClauseSet clauseSet = new ClauseSet("formula/formula01.cnf");
		System.out.println(clauseSet);
		assertTrue(clauseSet.unitPropagation());
	}

	@Test
	public void testUnitPropergation_Formula02() throws IOException {
		ClauseSet clauseSet = new ClauseSet("formula/formula02.cnf");
		System.out.println(clauseSet);
		assertTrue(clauseSet.unitPropagation());
	}
}
