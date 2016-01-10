package CDCLSolver;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import dataStructure.ClauseSet;

public class MaxSatTest {

	@Test
	public void testSolveFailure() throws IOException {
		System.out.println(new MaxSat(new ClauseSet("small_aim/no/aim-50-1_6-no-2.cnf")).nonPartionFuMalik());
		
//		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-1_6-no-2.cnf")).solve());
//		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-1_6-no-3.cnf")).solve());
//		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-1_6-no-4.cnf")).solve());
//		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-2_0-no-1.cnf")).solve());
//		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-2_0-no-2.cnf")).solve());
//		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-2_0-no-3.cnf")).solve());
//		assertNotNull(new CDCL(new ClauseSet("small_aim/no/aim-50-2_0-no-4.cnf")).solve());
	}
}
