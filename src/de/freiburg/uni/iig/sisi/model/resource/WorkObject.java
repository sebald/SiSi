package de.freiburg.uni.iig.sisi.model.resource;

import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class WorkObject extends ModelObject {
	
	private HashSet<Transition> usedBy = new HashSet<Transition>();

	public WorkObject(String id, String name, HashSet<Transition> transitions) {
		super(id, name);
		this.usedBy = transitions;
	}
	
	public HashSet<Transition> getTransitions() {
		return usedBy;
	}

	public void removeTransition(Transition transition) {
		usedBy.remove(transition);
	}
	
	public void addTransition(Transition transition) {
		usedBy.add(transition);
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	

}
