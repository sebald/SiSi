package de.freiburg.uni.iig.sisi.log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;
import de.freiburg.uni.iig.sisi.simulation.SimulationEvent;

public class LogGenerator implements PropertyChangeListener {

	public enum FileMode {
		CSV, MXML
	}

	public enum LogMode {
		LIST, COMPOSITE
	}
	
	private final FileMode fileMode;
	private TreeMap<String, EventLog> eventLogs = new TreeMap<String, EventLog>();

	private String currentSimulation = null;

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
	
	protected String getCurrentSimulation() {
		return currentSimulation;
	}

	protected void setCurrentSimulation(String currentSimulation) {
		this.currentSimulation = currentSimulation;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == SimulationEngine.PORPERTY_SIMULATION_START) {
			setCurrentSimulation((String) evt.getNewValue());
			eventLogs.put(getCurrentSimulation(), new EventLog());
		}
		if (evt.getPropertyName() == SimulationEngine.PROPERTY_TRANSITION_FIRED) {
			eventLogs.get(getCurrentSimulation()).addEvent((SimulationEvent) evt.getNewValue());
		}
		if (evt.getPropertyName() == SimulationEngine.PORPERTY_SIMULATION_COMPLETE) {
			//nothing yet
		}		
	}

	public String generateLog() throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH-mm-ss");
		Date date = new Date();
		return generateLog("logs/SiSiLog_"+ dateFormat.format(date)  + ".log");
	}

	public String generateLog(String uri) throws IOException {
		return generateLog(uri, LogMode.LIST, true);
	}
	
	public String generateLog(boolean createFile) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH-mm-ss");
		Date date = new Date();
		return generateLog("logs/SiSiLog_"+ dateFormat.format(date)  + ".log", LogMode.LIST, createFile);
	}
	
	public String geStringLog(String uri, LogMode logMode) throws IOException {
		return generateLog(uri, logMode, true);
	}
	
	public String generateLog(String uri, LogMode logMode, boolean createFile) throws IOException {
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
			File file = new File(uri);
			if ( !file.exists() )
				file.createNewFile();
			output = new BufferedWriter(new FileWriter(file));
			output.write(log);
			output.close();			
		}


		return log;
	}

	public String logToCSV(String id) {
		String log = "";
		for (SimulationEvent event : eventLogs.get(id).getEvents()) {
			log += event.toCSV() + System.getProperty("line.separator");
		}
		return log;
	}
	
	private String logsToCSV() {
		String log = "";
		for (EventLog eventLog : eventLogs.values()) {
			for (SimulationEvent event : eventLog.getEvents()) {
				log += event.toCSV() + System.getProperty("line.separator");
			}
		}
		return log;
	}
	
	public String getFullLog(){
		String log = "";
		for (EventLog eventLog : eventLogs.values()) {
			for (SimulationEvent event : eventLog.getEvents()) {
				log += event.toCSV() + System.getProperty("line.separator");
			}
		}		
		return log;
	}

}
