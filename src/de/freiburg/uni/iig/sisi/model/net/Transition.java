package de.freiburg.uni.iig.sisi.model.net;

import java.util.LinkedList;

public class Transition extends Node {
	
	private LinkedList<String> usedObject = new LinkedList<String>();

	public Transition(String id, String name) {
		super(id, name);
	}	
		
	public LinkedList<String> getUsedObject() {
		return usedObject;
	}

	public void addUsedObject(String usedObject) {
		this.usedObject.add(usedObject);
	}

	public boolean isFireable() {
		for (Arc arc : getIncomingArcs()) {
			if( ((Place) arc.getSource()).getMarking() == 0 )
			return false;
		}
		return true;
	}
	
}
