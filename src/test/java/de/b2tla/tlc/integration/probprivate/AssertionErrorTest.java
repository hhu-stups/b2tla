package de.b2tla.tlc.integration.probprivate;

import static de.b2tla.tlc.TLCResults.TLCResult.*;
import static de.b2tla.util.TestUtil.test;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.b2tla.tlc.TLCResults.TLCResult;
import de.b2tla.util.AbstractParseMachineTest;
import de.b2tla.util.PolySuite;
import de.b2tla.util.TestPair;
import de.b2tla.util.PolySuite.Config;
import de.b2tla.util.PolySuite.Configuration;

@RunWith(PolySuite.class)
public class AssertionErrorTest extends AbstractParseMachineTest {

	private final File machine;
	private final TLCResult error;

	public AssertionErrorTest(File machine, TLCResult result) {
		this.machine = machine;
		this.error = result;
	}

	@Test
	public void testRunTLC() throws Exception {
		String[] a = new String[] { machine.getPath() };
		assertEquals(error, test(a));
	}

	@Config
	public static Configuration getConfig() {
		final ArrayList<TestPair> list = new ArrayList<TestPair>();
		list.add(new TestPair(AssertionError, "../prob_examples/public_examples/TLC/AssertionError"));
		return getConfiguration(list);
	}
}
