package de.freiburg.uni.iig.sisi.view;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.widgets.Display;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.log.LogGenerator;
import de.freiburg.uni.iig.sisi.log.LogGenerator.FileMode;
import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.variant.NetDeviation.DeviationType;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration.ResourceSelectionMode;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;
import de.freiburg.uni.iig.sisi.simulation.SimulationExcpetion;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class SiSiViewController {
	
	// model
	private ProcessModel processModel = null;
	private SimulationConfiguration simulationConfiguration = null;
	private LogGenerator logGenerator = null;
	
	private boolean showLogView = true;
	private boolean autoSaveLogs = false;
	
	public ProcessModel getProcessModel() {
		return processModel;
	}

	public void loadModel(String path) throws ParserConfigurationException, SAXException, IOException{
		PNMLReader reader = new PNMLReader();
		processModel = reader.createModelFromPNML(path);
		simulationConfiguration = new SimulationConfiguration(ResourceSelectionMode.RANDOM, true);
		simulationConfiguration.setOriginalModel(processModel);
	}

	public void runSimulation() throws SimulationExcpetion, IOException{
		SimulationEngine se = new SimulationEngine(simulationConfiguration);
		logGenerator = new LogGenerator(se, simulationConfiguration.getFileMode());
		se.run();
		
		openLogView();
		
		String log = logGenerator.generateLog(false);
		System.out.println(log);
	}

	public LogGenerator getLogGenerator() {
		return logGenerator;
	}

	public boolean isShowLogView() {
		return showLogView;
	}

	public void setShowLogView(boolean showLogView) {
		this.showLogView = showLogView;
	}

	public boolean isAutoSaveLogs() {
		return autoSaveLogs;
	}

	public void setAutoSaveLogs(boolean autoSaveLogs) {
		this.autoSaveLogs = autoSaveLogs;
	}

	public void updateNumberOfIterations(int value) {
		simulationConfiguration.setNumberOfIterations(value);
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
	public void updateConfigParameter(Object object, int i) {
		// ModelObjects => means its an policy/uc
		if( object instanceof ModelObject )
			updateViolationParameter((ModelObject) object, i);
		if( object instanceof DeviationType )
			updateDeviationParameter((DeviationType) object, i);
	}
	
	public void updateViolationParameter(ModelObject modelObject, int i) {
		if( i == 0 ) {
			simulationConfiguration.removeFromViolationMap(modelObject);
			return;
		}
		simulationConfiguration.updateViolationMap(modelObject, i);		
	}
	
	public void updateDeviationParameter(DeviationType type, int i) {
		if( i == 0 ) {
			simulationConfiguration.removeFromDeviationMap(type);
			return;
		}
		simulationConfiguration.updateDeivationMap(type, i);
	}
	
	public void setFileMode(String input) {
		FileMode mode = FileMode.CSV;
		if( input.equals("MXML") )
			mode = FileMode.MXML;
		simulationConfiguration.setFileMode(mode);
	}
	
	public void setSaveLogPath(String path) {
		simulationConfiguration.setSaveLogPath(path);
	}
	
	public void setSeperateLogs(boolean seperate) {
		simulationConfiguration.setSeperateLogs(seperate);
	}
	
	protected void openLogView() {
		try {
			Display display = Display.getDefault();
			LogView shell = new LogView(this);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
