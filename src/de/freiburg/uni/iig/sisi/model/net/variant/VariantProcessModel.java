package de.freiburg.uni.iig.sisi.model.net.variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Arc;
import de.freiburg.uni.iig.sisi.model.net.Node;
import de.freiburg.uni.iig.sisi.model.net.Place;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.net.Transition.TransitionType;
import de.freiburg.uni.iig.sisi.model.net.variant.NetDeviation.DeviationType;
import de.freiburg.uni.iig.sisi.model.resource.WorkObject;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class VariantProcessModel extends ProcessModel {

	private NetDeviation deviation;
	
	public VariantProcessModel(ProcessModel pm) {
		this(pm, DeviationType.SKIPPING);
	}

	public VariantProcessModel(ProcessModel pm, DeviationType type) {
		PNMLReader reader = new PNMLReader();
		try {
			reader.cloneParametersFromDocTo(this, pm.getDoc());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
		long unixTime = System.currentTimeMillis() / 1000L;
		setId("variant#"+unixTime);
		setName("Variant for "+pm.getName());
		
		// after "cloning" everything, transform the net
		createDeviation(type);
	}
	
	public NetDeviation getDeviation() {
		return deviation;
	}

	public void setDeviation(NetDeviation deviation) {
		this.deviation = deviation;
	}

	private void createDeviation(DeviationType type) {
		deviation = new NetDeviation(type);
		
		if( type == DeviationType.SKIPPING ) {
			silenceTransition();
		} else if ( type == DeviationType.SWAPPING ) {
			swapTransitions();
		} else if ( type == DeviationType.AND2XOR ) {
			transformAnd2Xor();
		}
	}

	private void transformAnd2Xor() {
		// get random concurrency
		HashMap<Transition, Transition> andSplits = getNet().findConcurrency();
		List<Transition> keys = new ArrayList<Transition>(andSplits.keySet());
		Transition andSplit = keys.get( new Random().nextInt(keys.size()) );
		Transition andJoin = andSplits.get(andSplit);
		
		//merge postSet to one place
		ArrayList<Node> postSet = andSplit.getPostSet();
		String id = "";
		for (Node node : postSet) {
			id += node.getId();
		}
		// new merge place
		Place mergePlace = new Place(id, "", 0);
		getNet().addPlace(mergePlace);
		getNet().addInitialMarking(mergePlace);
		// (re)set arcs
		for (Node node : postSet) {
			for (Arc arc : node.getIncomingArcs()) {
				if( arc.getSource() == andSplit )
					node.removeIncomingArc(arc);
			}
			for (Arc arc : node.getOutgoingArcs()) {
				arc.setSource(mergePlace);
			}
			getNet().removePlace((Place) node);
		}
		// set one arc from andPlist to merge place
		Arc newArc = new Arc("a"+andSplit.getId()+mergePlace.getId(), andSplit, mergePlace);
		getNet().addArc(newArc);
		LinkedList<Arc> newOutgoingarcs = new LinkedList<Arc>();
		newOutgoingarcs.add(newArc);
		andSplit.setOutgoingArcs(newOutgoingarcs);		
		
		//merge preSet to one place
		ArrayList<Node> preSet = andJoin.getPreSet();
		id = "";
		for (Node node : preSet) {
			id += node.getId();
		}
		// new merge place
		Place joinPlace = new Place(id, "", 0);
		getNet().addPlace(joinPlace);
		getNet().addInitialMarking(joinPlace);
		// (re)set arcs
		for (Node node : preSet) {
			for (Arc arc : node.getOutgoingArcs()) {
				if( arc.getTarget() == andJoin )
					node.removeOutgoingArc(arc);
			}
			for (Arc arc : node.getIncomingArcs()) {
				arc.setTarget(joinPlace);
			}
			getNet().removePlace((Place) node);
		}
		// set one arc from andPlist to merge place
		newArc = new Arc("a"+joinPlace.getId()+andJoin.getId(), joinPlace, andJoin);
		getNet().addArc(newArc);
		LinkedList<Arc> newIncomingArcs = new LinkedList<Arc>();
		newIncomingArcs.add(newArc);
		andJoin.setIncomingArcs(newIncomingArcs);			
		
		// update markings
		getNet().reset();
	}

	private void swapTransitions() {
		// only swap transitions not involved in safety requirements
		ArrayList<Transition> transitions = new ArrayList<Transition>(getNonSafetyRequirementTransitions());
		Random generator = new Random();
		Object[] values = transitions.toArray();
		Transition transition1 = ((Transition) values[generator.nextInt(values.length)]);
		// find another to swap
		transitions.remove(transition1);
		Transition transition2 = null;
		// don't swap if the transitions are part of small concurrency
		do {
			values = transitions.toArray();
			transition2 = ((Transition) values[generator.nextInt(values.length)]);			
		} while ( getNet().partofSmallConcurrency(transition1, transition2) );
		// old values
		getDeviation().addOldValue(transition1);
		getDeviation().addOldValue(transition2);
		// swap
		String tmpID = transition1.getId();
		String tmpName = transition1.getName();
		HashSet<WorkObject> tmpWorkObjects = getResourceModel().getWorkObjectFor(transition1);
		transition1.setId(transition2.getId());
		transition1.setName(transition2.getName());
		HashSet<WorkObject> workObjects2 = getResourceModel().getWorkObjectFor(transition2);
		getResourceModel().setWorkObjectsFor(transition1, workObjects2);
		if( workObjects2 != null ) {
			for (WorkObject workObject : workObjects2) {
				workObject.removeTransition(transition2);
				workObject.addTransition(transition1);
			}				
		}
		transition2.setId(tmpID);
		transition2.setName(tmpName);
		getResourceModel().setWorkObjectsFor(transition2, tmpWorkObjects);
		if( tmpWorkObjects != null ) {
			for (WorkObject workObject : tmpWorkObjects) {
				workObject.removeTransition(transition1);
				workObject.addTransition(transition2);
			}
		}
		// new values
		getDeviation().addNewValue(transition1);
		getDeviation().addNewValue(transition2);		
	}

	private void silenceTransition() {
		Random generator = new Random();
		Object[] values = getNonEventuallyTransitions().toArray();
		Transition transition = ((Transition) values[generator.nextInt(values.length)]);
		getDeviation().addOldValue(transition);
		transition.setType(TransitionType.SILENT);
		getDeviation().addNewValue(transition);
	}
	
}
