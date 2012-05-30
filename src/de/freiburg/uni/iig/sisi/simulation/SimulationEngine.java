package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import de.freiburg.uni.iig.sisi.model.net.Arc;
import de.freiburg.uni.iig.sisi.model.net.Place;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Subject;

public class SimulationEngine {

	private final SimulationModel simulationModel;
	
	private LinkedList<Place> currentMarking = new LinkedList<Place>();
	private HashMap<String, Transition> fireableTransitions = new HashMap<String, Transition>();
	
	public SimulationEngine(SimulationModel simulationModel) {
		this.simulationModel = simulationModel;
		this.init();
	}

	private void init() {
		// init fireable transitions
		updateFireableTransitions();
	}

	private void updateFireableTransitions(){
		HashMap<String, Transition> fireableTransitions = new HashMap<String, Transition>();	
		for (Transition transition : simulationModel.getNet().getTransitions()) {
			if(transition.isFireable())
				fireableTransitions.put(transition.getId(), transition);
		}
		fireableTransitions = fireableTransitions;
	}
	
	private Transition getRandomFireableTransition(){
		Random generator = new Random();
		Object[] values = fireableTransitions.values().toArray();
		return (Transition) values[generator.nextInt(values.length)];

	}

	public void run() {
		while( !fireableTransitions.isEmpty() ) {
			Transition transition = getRandomFireableTransition();
			
			// internal (not observable operations)
			fire(transition);
			Subject subject = null;
			if(transition.getName() != null )
				subject = firedby(transition);
			
			// observable operations
			
		}
	}	
	
	private void fire(Transition transition) {		
		// we fire it we remove it
		fireableTransitions.remove(transition.getId());	
		// remove tokens form pre set
		for (Arc arc : transition.getIncomingArcs()) {
			Place p =  ((Place) arc.getSource());
			p.setMarking(p.getMarking() - 1);
		}
		// add tokens to post set
		for (Arc arc : transition.getOutgoingArcs()) {
			Place p =  ((Place) arc.getTarget());
			p.setMarking(p.getMarking() + 1);
		}		
		// check what is now fireable
		updateFireableTransitions();
	}

	private Subject firedby(Transition transition) {
		simulationModel.getResourceModel().getDomainFor(transition);
		return null;
	}
	
}
