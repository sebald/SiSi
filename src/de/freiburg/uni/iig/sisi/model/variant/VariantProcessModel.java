package de.freiburg.uni.iig.sisi.model.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class VariantProcessModel extends ProcessModel {

	public enum VariantType {
		SKIPPING, SWAPPING, AND2XOR, XOR2AND
	}
	
	public VariantProcessModel(String uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
		createDeviation(VariantType.SKIPPING);
	}

	public VariantProcessModel(String uri, VariantType type) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
		
		long unixTime = System.currentTimeMillis() / 1000L;
		setId("variant#"+unixTime);
		setName("Variant for "+getName());
		
		// after "cloning" everything transform the net
		createDeviation(type);
		
	}
	
	private void createDeviation(VariantType type) {
		if( type == VariantType.SKIPPING ) {
			silenceTransition();
		} else if ( type == VariantType.SWAPPING ) {
			swapTransitions();
		}
		
	}

	private void swapTransitions() {
		// only swap transitions not involved in safetryrequirements
		ArrayList<Transition> transitions = getNonSafetryRequirementTransitions();
		Random generator = new Random();
		Object[] values = transitions.toArray();
		Transition transition1 = ((Transition) values[generator.nextInt(values.length)]);
		// find another to swap
		transitions.remove(transition1);
		Transition transition2 = null;
		// don't swap if the transitions are part of small concurrency
		do {
			values = transitions.toArray();
			transition2 = ((Transition) values[generator.nextInt(values.length)]);			
		} while ( !getNet().partofSmallConcurrency(transition1, transition2) );

		System.out.println("Voher: "+transition1.getName());
		
		// swap
		String tmpID = transition1.getId();
		String tmpName = transition1.getName();
		transition1.setId(transition2.getId());
		transition1.setName(transition2.getName());
		transition2.setId(tmpID);
		transition2.setName(tmpName);
		
		System.out.println("nachher: "+transition1.getName());
		
	}

	private void silenceTransition() {
		Random generator = new Random();
		Object[] values = getNonEventuallyTransitions().toArray();
		((Transition) values[generator.nextInt(values.length)]).setName("");
	}
	
	
}
