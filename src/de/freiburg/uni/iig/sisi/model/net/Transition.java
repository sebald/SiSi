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

	/**
	 * Checks if the transition will be fireable in the future.
	 * This is true iff the pre-set of the transition is not a
	 * decision place (since the net has to be acyclic).
	 * 
	 * @return is the transition fireable in the future?
	 */
	public boolean canFireLater() {
		for (Arc arc : getIncomingArcs()) {
			if( arc.getSource().getOutgoingArcs().size() == 1 ) return true;
		}
		return false;
	}
}
