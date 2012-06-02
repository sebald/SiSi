package de.freiburg.uni.iig.sisi.model.variant;

import java.util.ArrayList;

import de.freiburg.uni.iig.sisi.model.net.Node;

public class NetDeviation {
	
	public enum DeviationType {
		SKIPPING, SWAPPING, AND2XOR, XOR2AND
	}
	
	private final DeviationType type;
	private ArrayList<Node> oldValues = new ArrayList<Node>();
	private ArrayList<Node> newValues = new ArrayList<Node>();
	
	public NetDeviation(DeviationType type) {
		this.type = type;
	}

	public DeviationType getType() {
		return type;
	}

	protected ArrayList<Node> getOldValues() {
		return oldValues;
	}

	/**
	 * Add old value. All references will be lost! Because only a clone is stored.
	 * 
	 * @param oldValue
	 */
	protected void addOldValue(Node oldValue) {
		this.oldValues.add(new Node(oldValue.getId(), oldValue.getName()));
	}

	protected ArrayList<Node> getNewValues() {
		return newValues;
	}

	/**
	 * Add new value. All references will be lost! Because only a clone is stored.
	 * 
	 * @param oldValue
	 */
	protected void addNewValue(Node newValue) {
		this.newValues.add(new Node(newValue.getId(), newValue.getName()));
	}

	
	
}
