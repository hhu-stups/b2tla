package de.tlc4b.prettyprint;

import static de.tlc4b.util.TestUtil.*;

import org.junit.Test;

public class SetsClauseTest {
	@Test
	public void testDefferedSet() throws Exception {
		String machine = "MACHINE test\n"
				+ "SETS d \n"
				+ "END";
		String expectedModule = "---- MODULE test----\n"
				+ "CONSTANTS d\n"
				+ "======";
		String expectedConfig = "CONSTANTS\n" +
				"d = {d1, d2, d3}\n";

		compareConfig(expectedModule, expectedConfig, machine);
	}
	
	@Test
	public void testEnumeratedSet() throws Exception {
		String machine = "MACHINE test\n"
				+ "SETS S = {a,b,c} \n"
				+ "END";
		String expectedModule = "---- MODULE test----\n"
				+ "CONSTANTS a, b, c\n"
				+ "S == {a, b, c}"
				+ "======";
		String expectedConfig = "CONSTANTS a = a\n b = b \n c = c"; 
		compareConfig(expectedModule, expectedConfig, machine);
	}
}