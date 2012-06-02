package de.freiburg.uni.iig.sisi.model;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

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
	
	public ProcessModel(String uri) throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		reader.createModelFromPNML(this, uri);
	}

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

	public void createFromPNML(String uri) throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		reader.createModelFromPNML(this, uri);
	}
	
	public ArrayList<Transition> getNonEventuallyTransitions(){
		ArrayList<Transition> transitions = getNet().getTransitions();
		// remove transitions that are eventually part (silencing it would cause conflicts)	
		transitions.removeAll(getSafetyRequirements().getEventuallyMap());
		return transitions;
	}
	
}
