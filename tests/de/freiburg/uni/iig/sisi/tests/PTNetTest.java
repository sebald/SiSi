package de.freiburg.uni.iig.sisi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Arc;
import de.freiburg.uni.iig.sisi.model.net.Node;
import de.freiburg.uni.iig.sisi.model.net.PTNet;
import de.freiburg.uni.iig.sisi.model.net.Place;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class PTNetTest {

	@Test
	public void testRun() throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		ProcessModel pm = reader.createModelFromPNML("examples/kbv.pnml");
		
		pm.getNet().reset();
		
//		assertNull("Run finished", pm.getNet().run());
	}
	
	@Test
	public void testFire() {
		PTNet n = new PTNet();
		Place p1 = new Place("0", "", 1);
		Place p2 = new Place("1", "", 0);
		n.addPlace(p1);
		n.addInitialMarking(p1);
		n.addPlace(p2);
		n.addInitialMarking(p2);
		n.addTransition(new Transition("2", ""));
		Arc arc1 = new Arc("02", n.getNode("0"), n.getNode("2"));
		Arc arc2 = new Arc("21", n.getNode("2"), n.getNode("1"));
		n.addArc(arc1);
		n.addArc(arc2);
		
		n.reset();
		n.fire();
		
		assertNull("Firing finished", n.fire());
	}	
	
	@Test
	public void testPartOfSmallConcurrency() throws ParserConfigurationException, SAXException, IOException {
		
		PTNet n = new PTNet();
		
		n.addPlace(new Place("0", "", 0));
		n.addPlace(new Place("1", "", 0));
		n.addPlace(new Place("2", "", 0));
		n.addPlace(new Place("3", "", 0));
		
		n.addTransition(new Transition("4", ""));
		n.addTransition(new Transition("5", ""));
		n.addTransition(new Transition("6", ""));
		n.addTransition(new Transition("7", ""));
		
		
		Arc arc40 = new Arc("40", n.getNode("4"), n.getNode("0"));
		Arc arc41 = new Arc("41", n.getNode("4"), n.getNode("1"));
		Arc arc05 = new Arc("05", n.getNode("0"), n.getNode("5"));
		Arc arc16 = new Arc("16", n.getNode("1"), n.getNode("6"));
		Arc arc52 = new Arc("52", n.getNode("5"), n.getNode("2"));
		Arc arc63 = new Arc("63", n.getNode("6"), n.getNode("3"));
		Arc arc27 = new Arc("27", n.getNode("2"), n.getNode("7"));
		Arc arc37 = new Arc("37", n.getNode("3"), n.getNode("7"));		
		
		n.addArc(arc40);
		n.addArc(arc41);
		n.addArc(arc05);
		n.addArc(arc16);
		n.addArc(arc52);
		n.addArc(arc63);
		n.addArc(arc27);
		n.addArc(arc37);
		
		assertEquals("Is part of small concurrency", true, n.partofSmallConcurrency((Transition) n.getNode("5"), (Transition) n.getNode("6")));
		
		PNMLReader reader = new PNMLReader();
		ProcessModel pm = reader.createModelFromPNML("examples/kbv.pnml");
		
		Transition t1 = (Transition) pm.getNet().getNode("t02");
		Transition t2 = (Transition) pm.getNet().getNode("t03");
		
		assertEquals("Is part of small concurrency", true, pm.getNet().partofSmallConcurrency(t1, t2));		
		
		
		Transition t3 = (Transition) pm.getNet().getNode("t06");
		
		assertEquals("Is part of small concurrency", false, pm.getNet().partofSmallConcurrency(t1, t3));	
		
	}

	@Test
	public void testGetSourcePlace() throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		ProcessModel pm = reader.createModelFromPNML("examples/kbv.pnml");
		Place p = pm.getNet().getSourcePlace();
		
		assertEquals("Is sink", true, p.getId().equals("p01"));
		assertEquals("Is sink", false, p.getId().equals("p09"));
	}	
	
	@Test
	public void testGetSinkPlace() throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		ProcessModel pm = reader.createModelFromPNML("examples/kbv.pnml");
		Place p = pm.getNet().getSinkPlace();
		
		assertEquals("Is sink", true, p.getId().equals("p09"));
		assertEquals("Is sink", false, p.getId().equals("p01"));
	}
	
	@Test
	public void testFindScopes() throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		PTNet net = reader.createModelFromPNML("examples/kbv.pnml").getNet();
		HashMap<Node, Node> scopes = net.findScopes();
		
		assertEquals("Has two scopes.", 2, scopes.size());
		
		assertEquals("Concurrency t1 -> t4", net.getNode("t04"), scopes.get(net.getNode("t01")));
		
		assertEquals("Decision p7 -> p8", net.getNode("p08"), scopes.get(net.getNode("p07")));
	}
	
	@Test
	public void testGenerateReachabilitySet() throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		ProcessModel pm = reader.createModelFromPNML("examples/kbv.pnml");
		
		pm.getNet().generateReachabilitySet();
		
		assertEquals("Size of reachability set.", 7, pm.getNet().getReachableTransitionsFor((Transition) pm.getNet().getNode("t01")).size());
		assertEquals("Size of reachability set.", 0, pm.getNet().getReachableTransitionsFor((Transition) pm.getNet().getNode("t08")).size());
	}
	
}
