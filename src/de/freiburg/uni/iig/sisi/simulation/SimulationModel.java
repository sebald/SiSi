package de.freiburg.uni.iig.sisi.simulation;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.net.PTNet;
import de.freiburg.uni.iig.sisi.model.resource.ResourceModel;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.SafetyRequirements;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class SimulationModel {
	
	private PTNet net = new PTNet();
	private ResourceModel rm = new ResourceModel();
	private SafetyRequirements safetyRequirements = new SafetyRequirements();
			
	public SimulationModel(String uri) throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		reader.createModelFromPNML(this, uri);
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
	
}
