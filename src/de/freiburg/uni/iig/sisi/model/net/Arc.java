package de.freiburg.uni.iig.sisi.model.net;

import de.freiburg.uni.iig.sisi.model.ModelObject;

public class Arc extends ModelObject {
	
	private Node source = null;
	private Node target = null;	

	public Arc(String id, Node source, Node target) {
		super(id, null);
		setSource(source);
		setTarget(target);
	}

	public Node getSource() {
		return source;
	}

	/**
	 * Set source {@link Node} for {@link Arc} and add {@link Arc} to the list
	 * of outgoing {@link Arc}s for the {@link Node}.
	 * 
	 * @param source
	 */
	public void setSource(Node source) {
		this.source = source;
		source.addOutgoingArc(this);
	}

	public Node getTarget() {
		return target;
	}
	
	/**
	 * Set source {@link Node} for {@link Arc} and add {@link Arc} to the list
	 * of incomming {@link Arc}s for the {@link Node}.
	 * 
	 * @param target
	 */
	public void setTarget(Node target) {
		this.target = target;
		target.addIncomingArc(this);
	}

}
