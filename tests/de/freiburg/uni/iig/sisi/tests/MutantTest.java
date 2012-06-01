package de.freiburg.uni.iig.sisi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.MutantFactory;

public class MutantTest {

	@Test
	public void testCreateMutantFromTransitionProcessModel() throws ParserConfigurationException, SAXException, IOException {
		ProcessModel pm = new ProcessModel("examples/kbv.pnml");
		Transition t = pm.getNet().getTransitions().get(0);
		MutantObject mutant = MutantFactory.createMutantFrom(t, pm);		
		HashSet<Subject> intersection = pm.getResourceModel().getDomainFor(t).getMembers();
		intersection.retainAll(mutant.getMutation());
		assertEquals("Is mutant", true, intersection.isEmpty());
	}

	@Test
	public void testCreateMutantFromPolicy() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testCreateMutantFromUsageControl() {
		fail("Not yet implemented"); // TODO
	}

}
