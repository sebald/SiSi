package de.freiburg.uni.iig.sisi.model.net;

public class Transition extends Node {
	
	public enum TransitionType {
		NORMAL, SILENT
	}
	
	private TransitionType type;
	
	public Transition(String id, String name) {
		super(id, name);
		this.setType(TransitionType.NORMAL);
	}
	
	public Transition(String id, String name, TransitionType type) {
		super(id, name);
		this.setType(type);
	}

	public TransitionType getType() {
		return type;
	}

	public void setType(TransitionType type) {
		this.type = type;
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

	public boolean isSilent() {
		if( getType() == TransitionType.SILENT ) return true;
		return false;
	}
}
