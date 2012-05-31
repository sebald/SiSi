package de.freiburg.uni.iig.sisi.simulation;

import java.sql.Timestamp;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.resource.WorkObject;

public class SimulationEvent {
	
	private final Transition transition;
	private final Subject subject;
	private final HashSet<WorkObject> usedObjects;
	private final Timestamp timestamp;
	
	public SimulationEvent(Transition transition, Subject subject, HashSet<WorkObject> usedObjects) {
		super();
		
		this.transition = transition;
		this.subject = subject;
		this.usedObjects = usedObjects;
		
		java.util.Date date= new java.util.Date();
		this.timestamp = new Timestamp(date.getTime());
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
		return "[" + this.transition.getName() + ", "+ this.subject.getName() + ", " + this.usedObjects + ", @" + this.timestamp + "]";
	}
	
	public String toCSV() {
		return this.timestamp + ", " + this.transition.getName() + ", "+ this.subject.getName() + ", " + this.usedObjects;
	}

}
