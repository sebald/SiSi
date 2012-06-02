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
		
		// after clonig everything the transform the net
		createDeviation(type);
		
	}
	
	private void createDeviation(VariantType type) {
		if( type == VariantType.SKIPPING ) {
			silenceTransition();
		}
		
	}

	private void silenceTransition() {
		ArrayList<Transition> transitions = getNet().getTransitions();
		// remove transitions that are eventually part (silencing it would cause conflicts)	
		transitions.removeAll(getSafetyRequirements().getEventuallyMap());
		
		// silence
		Random generator = new Random();
		Object[] values = transitions.toArray();
		((Transition) values[generator.nextInt(values.length)]).setName("");
		
	}

}
