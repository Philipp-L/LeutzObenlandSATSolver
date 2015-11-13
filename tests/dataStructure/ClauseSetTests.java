package dataStructure;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

public class ClauseSetTests {

	@Test
	public void testUnitPropergation_Success() {
		ClauseSet set = new ClauseSet(new Clause(new Vector<>(Arrays.asList(1)), null), 
				new Clause(new Vector<>(Arrays.asList(-1,2)), null), 
				new Clause(new Vector<>(Arrays.asList(1,2)), null));
		assertFalse(set.unitPropagation());
		assertEquals("", set.clausesToString());
	}
	@Test
	
	public void testUnitPropergation_Failure() {
		ClauseSet set = new ClauseSet(new Clause(new Vector<>(Arrays.asList(1)), null), 
				new Clause(new Vector<>(Arrays.asList(-1,2)), null), 
				new Clause(new Vector<>(Arrays.asList(-1,-2)), null));
		assertTrue(set.unitPropagation());
		assertEquals("{ }, sat = false, unassigned = 0\n", set.clausesToString());
	}

}
