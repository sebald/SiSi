package de.freiburg.uni.iig.sisi.model.net;

public class Place extends Node {
	
	public Place(String id, String name, int marking) {
		super(id, name);
		this.marking = marking;
	}

	private int marking = 0;

	public int getMarking() {
		return marking;
	}

	public void setMarking(int marking) {
		this.marking = marking;
	}

}
