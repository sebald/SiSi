package de.freiburg.uni.iig.sisi.model.net;

import de.freiburg.uni.iig.sisi.model.ModelObject;

public class Arc extends ModelObject {
	
	private Node source = null;
	private Node target = null;	

	public Arc(String id, Node source, Node target) {
		super(id, null);
		this.source = source;
		this.target = target;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

}
