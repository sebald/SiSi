package de.freiburg.uni.iig.sisi.view;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

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
	
	// model
	private ProcessModel processModel = null;
	private SimulationConfiguration simulationConfiguration = null;
	
	public ProcessModel getProcessModel() {
		return processModel;
	}

	public void loadModel(String path) throws ParserConfigurationException, SAXException, IOException{
		processModel = new ProcessModel(path);
		simulationConfiguration = new SimulationConfiguration(ResourceSelectionMode.RANDOM, true);
		simulationConfiguration.addProcessModel(processModel);
	}

	public void runSimulation() throws SimulationExcpetion, IOException{
		SimulationEngine se = new SimulationEngine(simulationConfiguration);
		LogGenerator lg = new LogGenerator(se, FileMode.CSV);
		se.runFor(1);
		String log = lg.generateLog(false);
		System.out.println(log);		
	}

	public void updateRunsWihtoutViolations(int value){
		simulationConfiguration.setRunsWithoutViolations(value);
	}
	
	public void updateRunsViolatingAuthorizations(int value) {
		simulationConfiguration.setRunsViolatingAuthorizations(value);
	}
	
	public void updateConsiderSafetyRequirements(boolean value) {
		simulationConfiguration.setConsiderSafetyRequirements(value);
	}
	
	/**
	 * Automatically adds, updates and deletes value form the config map.
	 * 
	 * @param mutantObject
	 * @param i set to {@code 0} will remove the parameter, otherwise it will be added or updated automatically
	 */
	public void updateConfigParameter(ModelObject modelObject, int i) {
		System.out.println(modelObject.getId() + " "+ i);
		if( i == 0 ) {
			simulationConfiguration.removeFromConfigMap(modelObject);
			return;
		}
		if( simulationConfiguration.getConfigMap().containsKey(modelObject) ) {
			simulationConfiguration.updateConfigMap(modelObject, i);
		} else {
			simulationConfiguration.addToConfigMap(modelObject, i);
		}
	}
	
}
