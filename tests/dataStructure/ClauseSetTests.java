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
		variables.put(-1, v1);
		variables.put(2, v2);
		variables.put(-2, v2);

		c1 = new Clause(new Vector<>(Arrays.asList(1)), variables);
		c2 = new Clause(new Vector<>(Arrays.asList(-1, 2)), variables);
		c3 = new Clause(new Vector<>(Arrays.asList(-1, -2)), variables);
	}

	/**
	 * Should be null, since there are no empty clauses and the clauses should
	 * be resolved
	 */
	@Test
	public void testUnitPropergation_Success() {
		ClauseSet set = new ClauseSet(variables, c1, c2);
		assertNull(set.unitPropagation());
	}

	/**
	 * Should not be null, since there is a conflict
	 * 
	 */
	@Test
	public void testUnitPropergation_Failure() {
		ClauseSet set = new ClauseSet(variables, c1, c2, c3);
		assertEquals(c2, set.unitPropagation());
	}

	@Test
	public void testUnitPropergation_Formula01() throws IOException {
		ClauseSet clauseSet = new ClauseSet("formula/formula01.cnf");
		Clause hasEmptyClause = clauseSet.unitPropagation();
		assertNull(hasEmptyClause);
	}

	@Test
	public void testUnitPropergation_Formula02() throws IOException {
		ClauseSet clauseSet = new ClauseSet("formula/formula02.cnf");
		Clause hasEmptyClause = clauseSet.unitPropagation();
		assertNotNull(hasEmptyClause);
	}
}
