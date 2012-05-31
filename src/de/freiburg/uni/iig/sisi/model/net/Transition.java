package de.freiburg.uni.iig.sisi.model.net;

public class Transition extends Node {
	
	public Transition(String id, String name) {
		super(id, name);
	}	

	public boolean isFireable() {
		for (Arc arc : getIncomingArcs()) {
			if( ((Place) arc.getSource()).getMarking() == 0 )
			return false;
		}
		return true;
	}
	
}
