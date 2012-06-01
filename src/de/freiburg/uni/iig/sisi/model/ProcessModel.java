package de.freiburg.uni.iig.sisi.model;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.net.PTNet;
import de.freiburg.uni.iig.sisi.model.resource.ResourceModel;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.SafetyRequirements;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class ProcessModel {
	
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
	
	public ResourceModel getResourceModel() {
		return resourceModel;
	}

	public SafetyRequirements getSafetyRequirements() {
		return safetyRequirements;
	}
	
}
