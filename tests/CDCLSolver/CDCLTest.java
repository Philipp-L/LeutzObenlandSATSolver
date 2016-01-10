package CDCLSolver;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import dataStructure.Clause;
import dataStructure.ClauseSet;
import dataStructure.Variable;

public class CDCLTest {
	Clause conflict, reason, reasonsReason;
	CDCL instance;
	Clause c1, c2, c3, c4;
	Variable a, b, w, z, y, x;
	HashMap<Integer, Variable> variables = new HashMap<>();
	Stack<Variable> variableStack = new Stack<>();

	@Before
	public void setUpBeforeClass() throws Exception {

		a = new Variable(1);
		b = new Variable(2);
		w = new Variable(3);
		z = new Variable(4);
		y = new Variable(5);
		x = new Variable(6);
		variables.clear();
		variables.put(1, a);
		variables.put(-1, a);
		variables.put(2, b);
		variables.put(-2, b);
		variables.put(3, w);
		variables.put(-3, w);
		variables.put(4, z);
		variables.put(-4, z);
		variables.put(5, y);
		variables.put(-5, y);
		variables.put(6, y);
		variables.put(-6, y);

		c1 = new Clause(new Vector<>(Arrays.asList(1)), variables);
		c2 = new Clause(new Vector<>(Arrays.asList(-1, 2)), variables);
		c3 = new Clause(new Vector<>(Arrays.asList(-1, -2)), variables);
		c4 = new Clause(new Vector<>(Arrays.asList(-1, 2, 3)), variables);

		conflict = new Clause(new Vector<Integer>(Arrays.asList(-1, -2, 3, 4, 5)), variables);
		reason = new Clause(new Vector<Integer>(Arrays.asList(-1, -2, 3, 4, -5)), variables);
		reasonsReason = new Clause(new Vector<Integer>(Arrays.asList(-1, -2, 3, -4, -6)), variables);

		ClauseSet set = new ClauseSet(variables, c1, c2, c3, c4, conflict, reason, reasonsReason);
		instance = new CDCL(set, variableStack, variables);

	}

	@Test
	public void testResolve() {
		Clause newClause = instance.resolve(c1, c2);
		Vector<Integer> newLiterals = newClause.getLiterals();
		assert (newLiterals.get(0) == 2);
		assert (newLiterals.size() == 1);

		newClause = instance.resolve(c2, c3);
		assert (newLiterals.get(0) == -1);
		assert (newLiterals.size() == 1);

		newClause = instance.resolve(c3, c4);
		assert (newLiterals.get(0) == -1);
		assert (newLiterals.get(1) == 3);
		assert (newLiterals.size() == 2);

		newClause = instance.resolve(conflict, reason);
		System.out.println(conflict);
		System.out.println(reason);
		Vector<Integer> expected = new Vector<Integer>(Arrays.asList(-1, -2, 3, 4));
		assertTrue(expected.containsAll(newClause.getLiterals()));
	}

	@Test
	public void testSolveFailure() throws IOException {
		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-1_6-no-1.cnf")).solve());
		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-1_6-no-2.cnf")).solve());
		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-1_6-no-3.cnf")).solve());
		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-1_6-no-4.cnf")).solve());
		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-2_0-no-1.cnf")).solve());
		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-2_0-no-2.cnf")).solve());
		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-2_0-no-3.cnf")).solve());
		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-2_0-no-4.cnf")).solve());
	}

	@Test
	public void testSolveSuccess() throws IOException {
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-1_6-yes1-1.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-1_6-yes1-2.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-1_6-yes1-3.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-1_6-yes1-4.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-2_0-yes1-1.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-2_0-yes1-2.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-2_0-yes1-3.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-2_0-yes1-4.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-3_4-yes1-1.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-3_4-yes1-2.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-3_4-yes1-3.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-3_4-yes1-4.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-6_0-yes1-1.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-6_0-yes1-2.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-6_0-yes1-3.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("small_aim/yes/aim-50-6_0-yes1-4.cnf")).solve());
	}
	@Test
	public void TestSolveSimple() throws IOException{
		assertNotNull(new CDCL(new ClauseSet("formula/formula01.cnf")).solve());
		assertNull(new CDCL(new ClauseSet("formula/formula02.cnf")).solve());
	}
	

}
