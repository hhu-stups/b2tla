package standard;


import java.util.HashSet;
import java.util.Hashtable;

import org.junit.Test;

import b2tla.analysis.Ast2String;
import b2tla.analysis.MachineContext;
import b2tla.analysis.PrecedenceCollector;
import b2tla.analysis.Typechecker;
import b2tla.analysis.UnchangedVariablesFinder;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.Start;

public class PrecedenceTest {

	
	
	@Test
	public void testDisjunctionConjunction() throws BException {
		String machine = "MACHINE test\n" 
				+ "PROPERTIES 1 = 1 & 2 = 2 or 3 = 3 \n"
				+ "END";
		new Env(machine);
	}

	
	@Test
	public void testMult() throws BException {
		String machine = "MACHINE test\n" 
				+ "PROPERTIES 1 * 2 * 3 = 6 \n"
				+ "END";
		 new Env(machine);
	}


	
	
	class Env {
		Hashtable<String, HashSet<Node>> unchangedVariablesOfOperation;

		public Env(String machine) throws BException {

			BParser parser = new BParser("Test");
			Start start = parser.parse(machine, false);
			final Ast2String ast2String2 = new Ast2String();
			start.apply(ast2String2);
			System.out.println(ast2String2.toString());

			MachineContext c = new MachineContext(start);
			Typechecker t = new Typechecker(c);
			UnchangedVariablesFinder u = new UnchangedVariablesFinder(c);
			
			PrecedenceCollector p = new PrecedenceCollector(start, t.getTypes());

		}

	}
}
