package de.freiburg.uni.iig.sisi.model.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.ModelObject;

public class PTNet extends ModelObject {

	private ArrayList<Place> places = new ArrayList<Place>();
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	private ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	// maps for quick reference
	private HashMap<String, Node> nodeMap = new HashMap<String, Node>();
	private HashMap<Place, Integer> initialMarking = new HashMap<Place, Integer>();
	private HashMap<Transition, HashSet<String>> workObjectsMap = new HashMap<Transition, HashSet<String>>();

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
	
	public HashMap<Transition, HashSet<String>> getWorkObjectsMap() {
		return workObjectsMap;
	}
	
	public void addWorkObject(Transition transition, String workObject) {
		if ( this.workObjectsMap.containsKey(transition) ){
			this.workObjectsMap.get(transition).add(workObject);
		} else {
			HashSet<String> objSet = new HashSet<String>();
			objSet.add(workObject);
			this.workObjectsMap.put(transition, objSet);
		}
	}
	
	public void reset(){
		for (Place place : places) {
			place.setMarking(initialMarking.get(place));
		}
	}
	
	/**
	 * Check if the {@link Transition}s are part of a small concurrency. Meaning every parallel
	 * path consists of only one {@link Transition}.
	 * 
	 * @param transition
	 * @return
	 */
	public boolean partofSmallConcurrency(Transition transition1, Transition transition2) {
		// preset of preset (T <- P <- T)
		ArrayList<Node> prePreSet1 = new ArrayList<Node>();
		for (Node node : transition1.getPreSet()) {
			prePreSet1.addAll(node.getPreSet());
		}
		ArrayList<Node> prePreSet2 = new ArrayList<Node>();
		for (Node node : transition2.getPreSet()) {
			prePreSet2.addAll(node.getPreSet());
		}
		// have common prepre transition
		prePreSet1.retainAll(prePreSet2);
		if ( prePreSet1.isEmpty() ) return false;
		
		// postset of postset (T -> P -> T)
		ArrayList<Node> postPostSet1 = new ArrayList<Node>();
		for (Node node : transition1.getPreSet()) {
			postPostSet1.addAll(node.getPreSet());
		}
		ArrayList<Node> postPostSet2 = new ArrayList<Node>();
		for (Node node : transition2.getPreSet()) {
			postPostSet2.addAll(node.getPreSet());
		}
		// have common prepre transition
		postPostSet1.retainAll(postPostSet2);
		if ( postPostSet1.isEmpty() ) return false;
		
		return true;
	}
	
	public Place getSourcePlace(){
		for (Place p : places) {
			if( p.getIncomingArcs().size() == 0 )
				return p;
		}
		return null;
	}
	
	public Place getSinkPlace(){
		for (Place p : places) {
			if( p.getOutgoingArcs().size() == 0 )
				return p;
		}
		return null;
	}	
	
	/**
	 * Search for scopes. E.g. fragments of the {@link PTNet} that starts with a split and end with an join (AND or XOR).
	 * This function is needed to transform the {@link PTNet} and create AND2XOR or XOR2AND deviations.
	 */
	public HashMap<Transition, Transition> findScopes() {
		
		
		
		
		return null;
	}
	
}
