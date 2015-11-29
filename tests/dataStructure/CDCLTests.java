package dataStructure;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class CDCLTests {
	Variable v1, v2;
	Clause c1, c2, c3, c4;
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
		c4 = new Clause(new Vector<>(Arrays.asList(2)), variables);
	}

	@Test
	public void testResolve() {
		CDCL cdcl = new CDCL(null);

		assertNull(cdcl.resolve(c1, c4));
		assertNull(cdcl.resolve(c2, c4));

		Vector<Integer> literals;
		literals = cdcl.resolve(c1, c2).getLiterals();
		assertEquals(1, literals.size());
		assertEquals(2, literals.get(0).intValue());

		literals = cdcl.resolve(c2, c1).getLiterals();
		assertEquals(1, literals.size());
		assertEquals(2, literals.get(0).intValue());

		literals = cdcl.resolve(c3, c4).getLiterals();
		assertEquals(1, literals.size());
		assertEquals(-1, literals.get(0).intValue());
	}

	@Test
	public void testGetNextVar() {
		CDCL cdcl = new CDCL(new ClauseSet(variables, c1, c2, c3, c4));

		v1.increaseActivity();
		assertEquals(v1, cdcl.getNextVar());

		v1.increaseActivity();
		assertEquals(v1, cdcl.getNextVar());

		v2.increaseActivity();
		assertEquals(v1, cdcl.getNextVar());

		v2.increaseActivity();
		v2.increaseActivity();
		assertEquals(v2, cdcl.getNextVar());

		v2.decreaseActivity();
		v2.decreaseActivity();
		assertEquals(v1, cdcl.getNextVar());
		
		v1.decreaseActivity();
		v1.decreaseActivity();
		assertEquals(v2, cdcl.getNextVar());
	}

}
