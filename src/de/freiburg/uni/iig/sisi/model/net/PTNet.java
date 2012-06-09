package de.freiburg.uni.iig.sisi.model.net;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import de.freiburg.uni.iig.sisi.model.ModelObject;

public class PTNet extends ModelObject {

	private ArrayList<Place> places = new ArrayList<Place>();
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	private ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	// maps for quick reference
	private HashMap<String, Node> nodeMap = new HashMap<String, Node>();
	private HashMap<Place, Integer> initialMarking = new HashMap<Place, Integer>();
	private HashMap<Transition, HashSet<String>> workObjectsMap = new HashMap<Transition, HashSet<String>>();
	private HashSet<Transition> fireableTransitions = new HashSet<Transition>();

	public ArrayList<Place> getPlaces() {
		return places;
	}
	
	public void addPlace(Place place) {
		this.places.add(place);
		this.nodeMap.put(place.getId(), place);
	}
	
	public void removePlace(Place place) {
		this.places.remove(place);
		this.nodeMap.remove(place.getId());
		this.initialMarking.remove(place);
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
	
	public void removeArc(Arc arc) {
		this.arcs.remove(arc);
	}
	
	protected void addNode(Node node) {
		this.nodeMap.put(node.getId(), node);
	}
	
	public Node getNode(String id){
		return this.nodeMap.get(id);
	}
	
	public Collection<Node> getNodes(){
		return this.nodeMap.values();
	}
	
	public HashMap<Place, Integer> getInitialMarking() {
		return initialMarking;
	}

	public void setInitialMarking(HashMap<Place, Integer> initialMarking) {
		this.initialMarking = initialMarking;
	}
	
	public void addInitialMarking(Place place) {
		this.initialMarking.put(place, place.getMarking());
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
	
	public Transition fire(){
		return fire(getRandomFireableTransition());
	}
	
	public Transition fire(Transition transition) {
		// remove tokens form pre set
		for (Arc arc : transition.getIncomingArcs()) {
			Place p = ((Place) arc.getSource());
			p.setMarking(p.getMarking() - 1);
		}
		// add tokens to post set
		for (Arc arc : transition.getOutgoingArcs()) {
			Place p = ((Place) arc.getTarget());
			p.setMarking(p.getMarking() + 1);
		}
		// check what is now fireable
		updateFireableTransitions();
		return transition;
	}

	private Transition getRandomFireableTransition() {
		Random generator = new Random();
		Object[] values = getFireableTransitions().toArray();
		return (Transition) values[generator.nextInt(values.length)];
	}	
	
	public HashSet<Transition> getFireableTransitions() {
		return fireableTransitions;
	}

	protected void setFireableTransitions(HashSet<Transition> fireableTransitions) {
		this.fireableTransitions = fireableTransitions;
	}

	public void updateFireableTransitions() {
		HashSet<Transition> fireableTransitions = new HashSet<Transition>();
		// add every transition that could be fired
		for (Transition transition : getTransitions()) {
			if (transition.isFireable()) {				
				fireableTransitions.add(transition);
			}
		}		
		this.setFireableTransitions(fireableTransitions);
	}	
	
	public void reset(){
		for (Place place : places) {
			place.setMarking(initialMarking.get(place));
		}
		updateFireableTransitions();
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
	
	public boolean isSplit(Node node) {
		if( node.getOutgoingArcs().size() > 1 ) return true;
		return false;
	}
	
	public boolean isJoin(Node node) {
		if( node.getIncomingArcs().size() > 1 ) return true;
		return false;		
	}
	
	/**
	 * Searches for scopes that begin and end with transitions. => AND
	 * 
	 * @return
	 */
	public HashMap<Transition, Transition> findConcurrencies(){
		HashMap<Transition, Transition> concurrencies = new HashMap<Transition, Transition>();
		for (Entry<Node, Node> entry : findScopes().entrySet()) {
			if( (entry.getKey() instanceof Transition) && (entry.getValue() instanceof Transition) )
				concurrencies.put((Transition) entry.getKey(), (Transition) entry.getValue());
		}
		return concurrencies;
	}

	/**
	 * Searches for scopes that begin and end with places. => XOR
	 * 
	 * @return
	 */
	public HashMap<Place, Place> findDecisions(){
		HashMap<Place, Place> decisions = new HashMap<Place, Place>();
		for (Entry<Node, Node> entry : findScopes().entrySet()) {
			if( (entry.getKey() instanceof Place) && (entry.getValue() instanceof Place) )
				decisions.put((Place) entry.getKey(), (Place) entry.getValue());
		}
		return decisions;
	}	
	
	/**
	 * Search for scopes. E.g. fragments of the {@link PTNet} that starts with a split and end with an join (AND or XOR).
	 * This function is needed to transform the {@link PTNet} and create AND2XOR or XOR2AND deviations.
	 */
	public HashMap<Node, Node> findScopes() {
		HashMap<Node, Node> scopes = new HashMap<Node, Node>();
		ArrayList<Node> splits = new ArrayList<Node>();
		for (Node node : getNodes()) {
			if( isSplit(node) )
				splits.add(node);
		}
		// for every split node find the join
		for (Node splitNode : splits) {
			Node joinNode = findFirstCommonNode(getFollowingNodes(splitNode));
			if( joinNode != null )
				scopes.put(splitNode, joinNode);
		}
		return scopes;
	}
	
	/**
	 * Walk ignores node types and firing (e.g. transition/place). Just walks over all possible
	 * paths and stores the {@link Node}s in visited order. <b>Warning:</b> This is a recursive
	 * method, so be sure that the net you are walking has a sink place!
	 * 
	 * @param node
	 * @param visitedNodes
	 * @return list of visited nodes
	 */
	public LinkedList<Node> walk(Node node, LinkedList<Node> visitedNodes) {
		visitedNodes.add(node);
		ArrayList<Node> postSet = node.getPostSet();
		if( postSet.size() == 0 )
			return visitedNodes;
		// depth first walk
		for (Node postNode : postSet) {
			walk(postNode, visitedNodes);
		}
		return visitedNodes;
	}
	
	/**
	 * Find all {@link Node}s that can be visited from the input {@code node}'s post set.
	 * 
	 * @param node
	 * @return paths starting from the post set of {@code node}. The key of the returned map is
	 * the starting node of the path
	 */
	public HashMap<Node, LinkedList<Node>> getFollowingNodes(Node node) {
		ArrayList<Node> postSet = node.getPostSet();
		HashMap<Node, LinkedList<Node>> visitedtNodes = new HashMap<Node, LinkedList<Node>>();
		for (Node postNode : postSet) {
			LinkedList<Node> pathNodes = new LinkedList<Node>();
			walk(postNode, pathNodes);
			visitedtNodes.put(postNode, pathNodes);
		}
		return visitedtNodes;
	}
	
	/**
	 * Find the first common not of some paths.
	 * 
	 * @param pathMap
	 * @return the first common node.
	 */
	public Node findFirstCommonNode(HashMap<Node, LinkedList<Node>> pathMap) {
		Node commonNode = null;
		
		// take a path and remove if from the map
		Entry<Node, LinkedList<Node>> entry = pathMap.entrySet().iterator().next();
		LinkedList<Node> referencePath = entry.getValue();
		pathMap.remove(entry.getKey());
		
		//iterate the path
		for (Node node : referencePath) {
			boolean common = false;
			// check if every other path also conains the node
			for (LinkedList<Node> path : pathMap.values()) {
				if( path.contains(node) ) {
					common = true;
				} else {
					common = false;
				}	
			}
			if( common ) {
				commonNode = node;
				break;
			}
		}
		return commonNode;
	}
	
}
