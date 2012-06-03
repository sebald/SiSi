package de.freiburg.uni.iig.sisi.view;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration.ResourceSelectionMode;

public class SiSiViewController {

	private ProcessModel processModel = null;
	private SimulationConfiguration simulationConfiguration = null;
	
	public ProcessModel getProcessModel() {
		return processModel;
	}

	protected void setProcessModel(ProcessModel processModel) {
		this.processModel = processModel;
	}

	public SimulationConfiguration getSimulationConfiguration() {
		return simulationConfiguration;
	}

	public void setSimulationConfiguration(SimulationConfiguration simulationConfiguration) {
		this.simulationConfiguration = simulationConfiguration;
	}

	public void loadModel(String path) throws ParserConfigurationException, SAXException, IOException{
		setProcessModel(new ProcessModel(path));
		setSimulationConfiguration(new SimulationConfiguration(ResourceSelectionMode.RANDOM, true));
	}	
	
}
