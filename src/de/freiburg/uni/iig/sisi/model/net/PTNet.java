package de.freiburg.uni.iig.sisi.model.net;

import java.util.ArrayList;
import java.util.HashMap;

import de.freiburg.uni.iig.sisi.model.ModelObject;

public class PTNet extends ModelObject {

	private ArrayList<Place> places = new ArrayList<Place>();
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	private ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	// map for quick reference and get
	private HashMap<String, Node> nodeMap = new HashMap<String, Node>();
	private HashMap<Place, Integer> initialMarking = new HashMap<Place, Integer>();

	public ArrayList<Place> getPlaces() {
		return places;
	}
	
	public void addPlace(Place place) {
		this.places.add(place);
		this.nodeMap.put(place.getId(), place);
	}
	
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}
	
	public void addTransition(Transition transition) {
		this.transitions.add(transition);
		this.nodeMap.put(transition.getId(), transition);
	}
	
	public ArrayList<Arc> getArcs() {
		return arcs;
	}
	
	public void addArc(Arc arc) {
		this.arcs.add(arc);
	}
	
	protected void addNode(Node node) {
		this.nodeMap.put(node.getId(), node);
	}
	
	public Node getNode(String id){
		return this.nodeMap.get(id);
	}
	
	public HashMap<Place, Integer> getInitialMarking() {
		return initialMarking;
	}

	public void setInitialMarking(HashMap<Place, Integer> initialMarking) {
		this.initialMarking = initialMarking;
	}
	
}
