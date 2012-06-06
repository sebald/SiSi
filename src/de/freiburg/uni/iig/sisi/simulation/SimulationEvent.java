package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.resource.WorkObject;

public class SimulationEvent {
	
	private final String simulationID;
	private final Transition transition;
	private final Subject subject;
	private HashSet<WorkObject> usedObjects = new HashSet<WorkObject>();
	
	public SimulationEvent(String simulationID, Transition transition, Subject subject, HashSet<WorkObject> usedObjects) {
		super();
		
		this.simulationID = simulationID;
		this.transition = transition;
		this.subject = subject;
		if( usedObjects != null )
			this.usedObjects.addAll(usedObjects);
	}

	public String getSimulationID() {
		return simulationID;
	}

	public Transition getTransition() {
		return transition;
	}

	public Subject getSubject() {
		return subject;
	}

	public HashSet<WorkObject> getUsedObjects() {
		return usedObjects;
	}

	@Override
	public String toString() {
		return "[" + this.getSimulationID() + ", " + this.transition.getName() + ", " + this.subject.getName() + ", " + this.usedObjects + "]";
	}
	
	public String toCSV() {
		return this.getSimulationID() + ", " + this.transition.getName() + ", "+ this.subject.getName() + ", " + this.usedObjects;
	}

}
