package de.freiburg.uni.iig.sisi.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.freiburg.uni.iig.sisi.model.net.Arc;
import de.freiburg.uni.iig.sisi.model.net.PTNet;
import de.freiburg.uni.iig.sisi.model.net.Place;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class PTNetTest {

	@Test
	public void testPartOfSmallConcurrency() {
		
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
	}

}
