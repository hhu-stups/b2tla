package de.b2tla.tlc.integration.probprivate;

import static de.b2tla.tlc.TLCOutput.TLCResult.WellDefinednessError;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.b2tla.B2TLA;
import de.b2tla.tlc.TLCOutput.TLCResult;
import de.b2tla.util.AbstractParseMachineTest;
import de.b2tla.util.PolySuite;
import de.b2tla.util.TestPair;
import de.b2tla.util.PolySuite.Config;
import de.b2tla.util.PolySuite.Configuration;

@RunWith(PolySuite.class)
public class WellDefinednessTest extends AbstractParseMachineTest  {

	

		private final File machine;
		private final TLCResult error;
		
		public WellDefinednessTest(File machine, TLCResult result) {
			this.machine = machine;
			this.error = result;
		}
		
		@Test
		public void testRunTLC() throws Exception {
			String[] a = new String[] { machine.getPath() };
			assertEquals(error, B2TLA.test(a,true));
		}

		@Config
		public static Configuration getConfig() {
			final ArrayList<TestPair> list = new ArrayList<TestPair>();
			list.add(new TestPair(WellDefinednessError, "../prob_examples/public_examples/TLC/WellDefinednessError"));
			return getConfiguration(list);
		}
}
