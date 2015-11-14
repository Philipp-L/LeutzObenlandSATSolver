package parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import org.junit.Test;

public class DimacsParserTests {
	@Test
	public void readFormula01() throws IOException {
		Vector<Vector<Integer>> expected = new Vector<Vector<Integer>>(
				Arrays.asList(new Vector<>(Arrays.asList(1, 2, -3)), new Vector<>(Arrays.asList(3, 4)),
						new Vector<>(Arrays.asList(-1)), new Vector<>(Arrays.asList(-2, 1)),
						new Vector<>(Arrays.asList(2, 3, -4))));
		;
		DimacsParser dimacsParser = new DimacsParser("formula/formula01.cnf");
		assertEquals(5, dimacsParser.getFormula().size());
		assertEquals(expected, dimacsParser.getFormula());
	}

	@Test
	public void readFormula02() throws IOException {
		Vector<Vector<Integer>> expected = new Vector<Vector<Integer>>(
				Arrays.asList(new Vector<>(Arrays.asList(1, 2, 3)), new Vector<>(Arrays.asList(-1, -2)),
						new Vector<>(Arrays.asList(1)), new Vector<>(Arrays.asList(4, -5, -3)),
						new Vector<>(Arrays.asList(-4, 5, -1)), new Vector<>(Arrays.asList(5, 2, 4)),
						new Vector<>(Arrays.asList(-1, 2, -4))));
		;
		DimacsParser dimacsParser = new DimacsParser("formula/formula02.cnf");
		assertEquals(7, dimacsParser.getFormula().size());
		assertEquals(expected, dimacsParser.getFormula());
	}
}
