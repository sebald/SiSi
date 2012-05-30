package de.freiburg.uni.iig.sisi.simulation;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.net.PTNet;
import de.freiburg.uni.iig.sisi.model.policies.SafetyRequirements;
import de.freiburg.uni.iig.sisi.model.resource.ResourceModel;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class SimulationModel {
	
	private PTNet net = new PTNet();
	private ResourceModel rm = new ResourceModel();
	private SafetyRequirements safetyRequirements = new SafetyRequirements();
	private HashMap<String, String> workObjectsMap = new HashMap<String, String>();
	
	public SimulationModel(String uri) throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		reader.createModelFromPNML(this, "examples/kbv.pnml");
	}

	public PTNet getNet() {
		return net;
	}
	
	public ResourceModel getResourceModel() {
		return rm;
	}

	public SafetyRequirements getSafetyRequirements() {
		return safetyRequirements;
	}

	
	public HashMap<String, String> getWorkObjectsMap() {
		return workObjectsMap;
	}
	
	public void addWorkObjectsMap(String transition, String workObject) {
		//TODO this could be more than one
		this.workObjectsMap.put(transition, workObject);
	}
	
	public void simulate(){
		
	}
	
}
