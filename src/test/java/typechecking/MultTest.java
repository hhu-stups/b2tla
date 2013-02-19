package typechecking;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import b2tla.exceptions.TypeErrorException;

import de.be4.classicalb.core.parser.exceptions.BException;

public class MultTest{

	
	@Test
	public void testMult() throws BException {
		String machine = "MACHINE test\n"
				+ "CONSTANTS k \n"
				+ "PROPERTIES 1 * 2 = k \n"
				+ "END";
		TestTypechecker t =  new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
	}
	
	@Test
	public void testMult2() throws BException {
		String machine = "MACHINE test\n"
				+ "CONSTANTS k \n"
				+ "PROPERTIES 1 * 2 * 3 = k \n"
				+ "END";
		TestTypechecker t =  new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
	}
	
	@Test
	public void testMult3() throws BException {
		String machine = "MACHINE test\n"
				+ "CONSTANTS k, k2, k3 \n"
				+ "PROPERTIES k * k2 * k3 = 4 \n"
				+ "END";
		TestTypechecker t =  new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
		assertEquals("INTEGER", t.constants.get("k3").toString());
	}
	
	@Test
	public void testMult4() throws BException {
		String machine = "MACHINE test\n"
				+ "CONSTANTS k, k2, k3 \n"
				+ "PROPERTIES k * 1 * k2 = k3 \n"
				+ "END";
		TestTypechecker t =  new TestTypechecker(machine);
		assertEquals("INTEGER", t.constants.get("k").toString());
		assertEquals("INTEGER", t.constants.get("k2").toString());
		assertEquals("INTEGER", t.constants.get("k3").toString());
	}
	
	@Test (expected = TypeErrorException.class)
	public void testMultException() throws BException {
		String machine = "MACHINE test\n"
				+ "CONSTANTS k, k2 \n"
				+ "PROPERTIES TRUE = k * k2 \n"
				+ "END";
		new TestTypechecker(machine);
	}
	
	@Test (expected = TypeErrorException.class)
	public void testMultException2() throws BException {
		String machine = "MACHINE test\n"
				+ "CONSTANTS k, k2 \n"
				+ "PROPERTIES  k = TRUE * k2 \n"
				+ "END";
		new TestTypechecker(machine);
	}
	
	@Test
	public void testCard1() throws BException {
		String machine = "MACHINE test\n"
				+ "CONSTANTS k, k2 \n"
				+ "PROPERTIES k * k2 = {1} * {TRUE} \n"
				+ "END";
		TestTypechecker t =  new TestTypechecker(machine);
		assertEquals("POW(INTEGER)", t.constants.get("k").toString());
		assertEquals("POW(BOOL)", t.constants.get("k2").toString());
	}
	
	@Test
	public void testCard2() throws BException {
		String machine = "MACHINE test\n"
				+ "CONSTANTS k, k2, k3 \n"
				+ "PROPERTIES k * k2 * k3 = {TRUE} * {1} * {TRUE} \n"
				+ "END";
		TestTypechecker t =  new TestTypechecker(machine);
		assertEquals("POW(BOOL)", t.constants.get("k").toString());
		assertEquals("POW(INTEGER)", t.constants.get("k2").toString());
		assertEquals("POW(BOOL)", t.constants.get("k3").toString());
	}
	
}
