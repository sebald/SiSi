package de.freiburg.uni.iig.sisi.view;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.widgets.Spinner;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.log.LogGenerator;
import de.freiburg.uni.iig.sisi.log.LogGenerator.FileMode;
import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration.ResourceSelectionMode;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;
import de.freiburg.uni.iig.sisi.simulation.SimulationExcpetion;

public class SiSiViewController {

	private ProcessModel processModel = null;
	private SimulationConfiguration simulationConfiguration = null;
	
	//mappings between model and view elements
	private HashMap<ModelObject, Spinner> modelObjectSpinnerMap = new HashMap<ModelObject, Spinner>();
	
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
		processModel = new ProcessModel(path);
		simulationConfiguration = new SimulationConfiguration(ResourceSelectionMode.RANDOM, true);
		simulationConfiguration.addProcessModel(processModel);
	}

	protected HashMap<ModelObject, Spinner> getModelObjectSpinnerMap() {
		return modelObjectSpinnerMap;
	}

	protected void addModelObjectSpinnerMap(ModelObject mo, Spinner spinner) {
		this.modelObjectSpinnerMap.put(mo, spinner);
	}

	public void runSimulation() throws SimulationExcpetion, IOException{
		SimulationEngine se = new SimulationEngine(simulationConfiguration);
		LogGenerator lg = new LogGenerator(se, FileMode.CSV);
		se.runFor(1);
		String log = lg.generateLog(false);
		System.out.println(log);		
	}
	
}
