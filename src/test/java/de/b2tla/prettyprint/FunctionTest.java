package de.b2tla.prettyprint;

import static de.b2tla.util.TestUtil.*;
import org.junit.Test;



public class FunctionTest {

	@Test
	public void testLambdaAbstraction() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES [1] = %x.(x = 1 | 1)\n"
				+ "END";
		String expected = "---- MODULE test----\n"
				+ "EXTENDS Integers\n"
				+ "ASSUME <<1>> = [x \\in {1} |-> 1 ]\n"
				+ "======";
		compare(expected, machine);
	}
	
	@Test
	public void testLambdaAbstraction2() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES [1] = %x.(x = 1 & 1 = 1| 1)\n"
				+ "END";
		String expected = "---- MODULE test----\n"
				+ "EXTENDS Integers\n"
				+ "ASSUME <<1>> = [x \\in {x \\in {1}: 1=1} |-> 1 ]\n"
				+ "======";
		compare(expected, machine);
	}
	
	@Test
	public void testFunctionApplication() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES 1 = %x.(x = 1 | 1)(1)\n"
				+ "END";
		String expected = "---- MODULE test----\n"
				+ "ASSUME 1 = [x \\in {1} |-> 1 ][1]\n"
				+ "======";
		compare(expected, machine);
	}
	
	@Test
	public void testFunctionApplicationMoreArguments() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES 4 = %a,b.(a=b | a+b)(2,2)\n"
				+ "END";
		String expected = "---- MODULE test ----\n"
				+ "EXTENDS Integers\n"
				+ "ASSUME 4 = [<<a, b>> \\in { <<a, b>> \\in ((-1..4) \\times (-1..4)) : a = b} |-> a + b][2, 2]\n"
				+ "====";
		compare(expected, machine);
	}
	
	
	@Test
	public void testFunctionVsRelation2() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES {(1|->2|->3)} = %a,b.(a = b | a+b)\n"
				+ "END";
		String expected = "---- MODULE test ----\n"
				+ "EXTENDS Integers\n"
				+ "ASSUME {<<<<1, 2>>,3>>} = {<<<<a, b>>, a + b>> : <<a, b>> \\in { <<a, b>> \\in ((-1..4) \\times (-1..4)) : a = b}}\n"
				+ "====";
		compare(expected, machine);
	}

	@Test
	public void testDomain() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES {1} = dom(%x.(x = 1 | 1))\n"
				+ "END";
		String expected = "---- MODULE test----\n"
				+ "ASSUME {1} = DOMAIN [x \\in {1} |-> 1 ]\n"
				+ "======";
		compare(expected, machine);
	}
	
	@Test
	public void testSequenceFunctionCall() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES [1,2](1) = 1\n"
				+ "END";
		String expected = "---- MODULE test----\n"
				+ "ASSUME <<1, 2>>[1] = 1\n"
				+ "======";
		compare(expected, machine);
	}
	
	@Test
	public void testTotalFunction() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES {1} --> {1} = {1} --> {1}\n" + "END";
		String expected = "---- MODULE test ----\n"
				+ "ASSUME [{1} -> {1}] = [{1} -> {1}]\n"
				+ "====";
		compareEquals(expected, machine);
	}
	

	@Test
	public void testPartialFunction() throws Exception {
		String machine = "MACHINE test\n"
				+ "PROPERTIES {} = {1,2} +-> {1,2}\n" + "END";
		String expected = "---- MODULE test ----\n"
				+ "EXTENDS FunctionsAsRelations\n"
				+ "ASSUME {} = RelParFunc({1, 2}, {1, 2})\n"
				+ "====";
		compareEquals(expected, machine);
	}
	
}
