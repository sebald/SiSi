package de.freiburg.uni.iig.sisi.log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.TreeMap;

import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;

public class LogGenerator implements PropertyChangeListener {

	public enum FileMode {
		CSV, MXML
	}
	
	private final FileMode fileMode;
	private TreeMap<String, EventLog> eventLogs = new TreeMap<String, EventLog>();
	private TreeMap<String, MutationEvent> mutationLog = new TreeMap<String, MutationEvent>();
	private TreeMap<String, ProcessInstanceInformation> modelMap = new TreeMap<String, ProcessInstanceInformation>();

	private String currentSimulationID = null;

	public LogGenerator(SimulationEngine se) {
		se.addChangeListener(this);
		this.fileMode = FileMode.CSV;
	}

	public LogGenerator(SimulationEngine se, FileMode fileMode) {
		se.addChangeListener(this);
		this.fileMode = fileMode;
	}
	
	public TreeMap<String, EventLog> getEventLogs() {
		return eventLogs;
	}	
	
	public TreeMap<String, MutationEvent> getMutationLog() {
		return mutationLog;
	}

	public void addMutationEvent(MutationEvent mutationEvent) {
		this.mutationLog.put(mutationEvent.getSimulationID(), mutationEvent);
	}

	public TreeMap<String, ProcessInstanceInformation> getModelMap() {
		return modelMap;
	}

	protected String getCurrentSimulationID() {
		return currentSimulationID;
	}

	protected void setCurrentSimulationID(String currentSimulation) {
		this.currentSimulationID = currentSimulation;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == SimulationEngine.PORPERTY_SIMULATION_START) {
			setCurrentSimulationID((String) evt.getNewValue());
			eventLogs.put(getCurrentSimulationID(), new EventLog());
		}
		if (evt.getPropertyName() == SimulationEngine.PROPERTY_TRANSITION_FIRED) {
			eventLogs.get(getCurrentSimulationID()).addEvent((SimulationEvent) evt.getNewValue());
		}
		if (evt.getPropertyName() == SimulationEngine.PORPERTY_SIMULATION_COMPLETE) {
			modelMap.put(getCurrentSimulationID(), (ProcessInstanceInformation) evt.getNewValue());
		}
		if ( evt.getPropertyName() == SimulationEngine.PROPERTY_MUTATION_EXECUTED ) {
			MutationEvent mutationEvent = ((MutationEvent) evt.getNewValue());
			mutationLog.put(mutationEvent.getSimulationID(), mutationEvent);
		}
	}

	public String generateLog(String path, boolean createFile) throws IOException {
		// parse log
		String log = "";
		if ( fileMode == FileMode.MXML ) {
			log = logsToCSV();
		} else {
			log = logsToCSV();
		}
		
		// create file
		if ( createFile ) {
			Writer output = null;
			File file = new File(path);
			if ( !file.exists() )
				file.createNewFile();
			output = new BufferedWriter(new FileWriter(file));
			output.write(log);
			output.close();			
		}
		generateViolationLog(path, true);
		return log;
	}
	
	public String generateLogFromID(String id, String path, boolean createFile) throws IOException {
		// parse log
		String log = "";
		if ( fileMode == FileMode.MXML ) {
			log = logToCSV(id);
		} else {
			log = logToCSV(id);
		}
		
		// create file
		if ( createFile ) {
			Writer output = null;
			File file = new File(path);
			if ( !file.exists() )
				file.createNewFile();
			output = new BufferedWriter(new FileWriter(file));
			output.write(log);
			output.close();			
		}

		if ( !mutationLog.isEmpty() ) {
			String violationPath = new String(path.substring(0, path.lastIndexOf('.')));
			violationPath += "_violationData" + new String(path.substring(path.lastIndexOf('.')));
			generateLogFromID(id, violationPath, true);
		}		
		generateViolationLogFromID(id, path, true);
		return log;		
	}
	
	private String generateViolationLog(String path, boolean createFile) throws IOException {
		if ( mutationLog.isEmpty() )
			return null;
		
		String violationPath = new String(path.substring(0, path.lastIndexOf('.')));
		violationPath += "_violationData" + new String(path.substring(path.lastIndexOf('.')));
		
		String log = "";
		if ( fileMode == FileMode.MXML ) {
			log = logsToCSV();
		} else {
			log = logsToCSV();
		}
		
		// create file
		if ( createFile ) {
			Writer output = null;
			File file = new File(violationPath);
			if ( !file.exists() )
				file.createNewFile();
			output = new BufferedWriter(new FileWriter(file));
			output.write(log);
			output.close();			
		}	
		return log;
	}
	
	private String generateViolationLogFromID(String id, String path, boolean createFile) throws IOException {
		if ( !mutationLog.containsKey(id) )
			return null;
		
		String violationPath = new String(path.substring(0, path.lastIndexOf('.')));
		violationPath += "_violationData" + new String(path.substring(path.lastIndexOf('.')));		
		// parse log
		String log = "";
		if ( fileMode == FileMode.MXML ) {
			log = logToCSV(id);
		} else {
			log = logToCSV(id);
		}
		
		// create file
		if ( createFile ) {
			Writer output = null;
			File file = new File(violationPath);
			if ( !file.exists() )
				file.createNewFile();
			output = new BufferedWriter(new FileWriter(file));
			output.write(log);
			output.close();			
		}
		return null;
	}	
	
	public String logToCSV(String id) {
		String log = "";
		for (SimulationEvent event : eventLogs.get(id).getEvents()) {
			log += event.toCSV() + System.getProperty("line.separator");
		}
		return log;
	}
	
	public String logsToCSV(){
		String log = "";
		for (EventLog eventLog : eventLogs.values()) {
			for (SimulationEvent event : eventLog.getEvents()) {
				log += event.toCSV() + System.getProperty("line.separator");
			}
		}		
		return log;
	}

}
