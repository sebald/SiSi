package de.freiburg.uni.iig.sisi.model.net;

import java.util.ArrayList;
import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.model.ModelObject;

public class Node extends ModelObject {
	
	public Node(String id, String name) {
		super(id, name);
	}

	private LinkedList<Arc> incomingArcs = new LinkedList<Arc>();
	private LinkedList<Arc>	outgoingArcs = new LinkedList<Arc>();
	
	public void setIncomingArcs(LinkedList<Arc> incomingArcs) {
		this.incomingArcs = incomingArcs;
	}

	public void setOutgoingArcs(LinkedList<Arc> outgoingArcs) {
		this.outgoingArcs = outgoingArcs;
	}
	
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

	public ArrayList<Node> getPreSet(){
		ArrayList<Node> preSet = new ArrayList<Node>();
		for (Arc arc : getIncomingArcs()) {
			preSet.add(arc.getSource());
		}
		return preSet;
	}
	
	public ArrayList<Node> getPostSet(){
		ArrayList<Node> posSet = new ArrayList<Node>();
		for (Arc arc : getOutgoingArcs()) {
			posSet.add(arc.getTarget());
		}
		return posSet;
	}	
	
}
