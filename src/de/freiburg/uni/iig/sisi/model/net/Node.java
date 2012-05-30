package de.freiburg.uni.iig.sisi.model.net;

import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.model.ModelObject;

public class Node extends ModelObject {
	
	public Node(String id, String name) {
		super(id, name);
	}

	private LinkedList<Arc> incomingArcs = new LinkedList<Arc>();
	private LinkedList<Arc>	outgoingArcs = new LinkedList<Arc>();
	
	public LinkedList<Arc> getIncomingArcs() {
		return incomingArcs;
	}
	
	public void addIncomingArc(Arc arc) {
		this.incomingArcs.add(arc);
	}
	
	public void removeIncomingArc(Arc arc) {
		this.incomingArcs.remove(arc);
	}
	
	public LinkedList<Arc> getOutgoingArcs() {
		return outgoingArcs;
	}
	
	public void addOutgoingArc(Arc arc) {
		this.outgoingArcs.add(arc);
	}
	
	public void removeOutgoingArc(Arc arc) {
		this.outgoingArcs.remove(arc);
	}	

}
