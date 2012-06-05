package de.freiburg.uni.iig.sisi.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy.PolicyType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.AuthorizationMutant;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.MutantFactory;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.PolicyMutant;
import de.freiburg.uni.iig.sisi.simulation.SimulationEvent;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class MutantTest {

	@Test
	public void testCreateMutantFromTransitionProcessModel() throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		ProcessModel pm = reader.createModelFromPNML("examples/kbv.pnml");
		Transition t = pm.getNet().getTransitions().get(0);
		AuthorizationMutant mutant = (AuthorizationMutant) MutantFactory.createMutantFor(t, pm);
		HashSet<Subject> intersection = new HashSet<Subject>();
		for (Role role : pm.getResourceModel().getDomainFor(t)) {
			intersection.addAll(role.getMembers());
		}
		intersection.retainAll(mutant.getMutation());
		assertEquals("Is mutant", true, intersection.isEmpty());
		
		Transition t1 = (Transition) pm.getNet().getNode("t07");
		mutant = (AuthorizationMutant) MutantFactory.createMutantFor(t1, pm);
		intersection = new HashSet<Subject>();
		for (Role role : pm.getResourceModel().getDomainFor(t1)) {
			intersection.addAll(role.getMembers());
		}
		intersection.retainAll(mutant.getMutation());
		assertEquals("Is mutant", true, intersection.isEmpty());		
	}

	@Test
	public void testCreateMutantFromPolicy() throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		ProcessModel pm = reader.createModelFromPNML("examples/kbv.pnml");
		SimulationEvent event = new SimulationEvent("asd", (Transition) pm.getNet().getNode("t04"), pm.getResourceModel().getSubject("s01"), null);
		
		// SoD
		Policy policySoD = new Policy("p01", "", PolicyType.SEPERATION_OF_DUTY, (Transition) pm.getNet().getNode("t04"), (Transition) pm.getNet().getNode("t05"));
		PolicyMutant mSoD = (PolicyMutant) MutantFactory.createMutantFor(policySoD, pm);
		HashSet<Subject> badSubjects = mSoD.getMutation(event);
		badSubjects.remove(pm.getResourceModel().getSubject("s01"));
		assertEquals("Is mutant", true, badSubjects.isEmpty());
		
		//BoD
		Policy policyBoD = new Policy("p01", "", PolicyType.BINDING_OF_DUTY, (Transition) pm.getNet().getNode("t04"), (Transition) pm.getNet().getNode("t05"));
		PolicyMutant mBoD = (PolicyMutant) MutantFactory.createMutantFor(policyBoD, pm);
		badSubjects = mBoD.getMutation(event);
		
		badSubjects.add(pm.getResourceModel().getSubject("s01"));
		HashSet<Subject> tmpSubjects = new HashSet<Subject>();
		for (Role role :pm.getResourceModel().getDomainFor((Transition) pm.getNet().getNode("t05"))) {
			tmpSubjects.addAll(role.getMembers());
		}
		tmpSubjects.removeAll(badSubjects);
		
		assertEquals("Is mutant", false, tmpSubjects.contains(pm.getResourceModel().getSubject("s01")));
		
		//CoI	
		Policy policyCoI = new Policy("p01", "", PolicyType.CONFLICT_OF_INTEREST, (Transition) pm.getNet().getNode("t04"), (Transition) pm.getNet().getNode("t05"));
		PolicyMutant mCoI = (PolicyMutant) MutantFactory.createMutantFor(policyCoI, pm);
		badSubjects = mCoI.getMutation(event);
		
		assertEquals("Is mutant", true, badSubjects.isEmpty());
		
	}

	@Test
	public void testCreateMutantFromUsageControl() {
//		fail("Not yet implemented"); // TODO
	}

}
