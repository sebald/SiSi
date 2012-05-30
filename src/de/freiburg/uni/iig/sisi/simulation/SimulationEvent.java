package de.freiburg.uni.iig.sisi.simulation;

import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Subject;

public class SimulationEvent {
	
	private final Transition transition;
	private final Subject subject;
	private final LinkedList<String> usedObjects;
	
	public SimulationEvent(Transition transition, Subject subject, LinkedList<String> usedObjects) {
		super();
		this.transition = transition;
		this.subject = subject;
		this.usedObjects = usedObjects;
	}

	public Transition getTransition() {
		return transition;
	}

	public Subject getSubject() {
		return subject;
	}

	public LinkedList<String> getUsedObjects() {
		return usedObjects;
	}

	@Override
	public String toString() {
		return "[" + this.transition.getName() + ", "+ this.subject.getName() + ", " + this.usedObjects + "]";
	}

}
