package de.freiburg.uni.iig.sisi.model.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.variant.NetDeviation.DeviationType;

public class VariantProcessModel extends ProcessModel {

	private NetDeviation deviation;
	
	public VariantProcessModel(String uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
		createDeviation(DeviationType.SKIPPING);
	}

	public VariantProcessModel(String uri, DeviationType type) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
		
		long unixTime = System.currentTimeMillis() / 1000L;
		setId("variant#"+unixTime);
		setName("Variant for "+getName());
		
		// after "cloning" everything transform the net
		createDeviation(type);
	}
	
	public NetDeviation getDeviation() {
		return deviation;
	}

	public void setDeviation(NetDeviation deviation) {
		this.deviation = deviation;
	}

	private void createDeviation(DeviationType type) {
		deviation = new NetDeviation(type);
		
		if( type == DeviationType.SKIPPING ) {
			silenceTransition();
		} else if ( type == DeviationType.SWAPPING ) {
			swapTransitions();
		}
	}

	private void swapTransitions() {
		// only swap transitions not involved in safety requirements
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
		// old values
		getDeviation().addOldValue(transition1);
		getDeviation().addOldValue(transition2);
		// swap
		String tmpID = transition1.getId();
		String tmpName = transition1.getName();
		transition1.setId(transition2.getId());
		transition1.setName(transition2.getName());
		transition2.setId(tmpID);
		transition2.setName(tmpName);
		// new values
		getDeviation().addNewValue(transition1);
		getDeviation().addNewValue(transition2);
	}

	private void silenceTransition() {
		Random generator = new Random();
		Object[] values = getNonEventuallyTransitions().toArray();
		Transition transition = ((Transition) values[generator.nextInt(values.length)]);
		getDeviation().addOldValue(transition);
		transition.setName("");
		getDeviation().addNewValue(transition);
	}
	
}
