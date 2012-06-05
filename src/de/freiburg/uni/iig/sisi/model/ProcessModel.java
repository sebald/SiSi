package de.freiburg.uni.iig.sisi.model;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.net.PTNet;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.ResourceModel;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.SafetyRequirements;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class ProcessModel extends ModelObject {
	
	private PTNet net = new PTNet();
	private ResourceModel resourceModel = new ResourceModel();
	private SafetyRequirements safetyRequirements = new SafetyRequirements();
	
	// keep xml doc for cloning
	private  Document doc;
	
	public PTNet getNet() {
		return net;
	}

	public void setNet(PTNet net) {
		this.net = net;
	}

	public ResourceModel getResourceModel() {
		return resourceModel;
	}

	public void setResourceModel(ResourceModel resourceModel) {
		this.resourceModel = resourceModel;
	}

	public SafetyRequirements getSafetyRequirements() {
		return safetyRequirements;
	}

	public void setSafetyRequirements(SafetyRequirements safetyRequirements) {
		this.safetyRequirements = safetyRequirements;
	}

	public Document getDoc() {
		return doc;
	}
	
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	public ProcessModel clone() {
		PNMLReader reader = new PNMLReader();
		try {
			return reader.cloneParametersFromDoc(doc);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Transition> getNonEventuallyTransitions() {
		ArrayList<Transition> transitions = new ArrayList<Transition>(getNet().getTransitions());
		transitions.removeAll(getSafetyRequirements().getEventuallyMap());
		return transitions;
	}
	
	public ArrayList<Transition> getNonObjetiveTransitions() {
		ArrayList<Transition> transitions = new ArrayList<Transition>(getNet().getTransitions());
		transitions.removeAll(getSafetyRequirements().getObjectiveMap());
		return transitions;		
	}
	
	public ArrayList<Transition> getNonSafetryRequirementTransitions() {
		ArrayList<Transition> nonEventuallyTransitions = new ArrayList<Transition>(getNonEventuallyTransitions());
		ArrayList<Transition> nonObjectiveTransitions = new ArrayList<Transition>(getNonObjetiveTransitions());
		nonEventuallyTransitions.retainAll(nonObjectiveTransitions);
		return nonEventuallyTransitions;
	}
	
}
