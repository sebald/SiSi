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

	@Override
	public String toString() {
		return this.getName();
	}
	

}
