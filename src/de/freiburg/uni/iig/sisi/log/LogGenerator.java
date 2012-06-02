package de.freiburg.uni.iig.sisi.log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
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
	
	private FileMode fileMode;
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
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == SimulationEngine.PORPERTY_SIMULATION_START) {
			currentSimulation = (String) evt.getNewValue();
			eventLogs.put((String) evt.getNewValue(), new EventLog());
		}
		if (evt.getPropertyName() == SimulationEngine.PROPERTY_TRANSITION_FIRED) {
			eventLogs.get(currentSimulation).addEvent((SimulationEvent) evt.getNewValue());
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
		return generateLog(uri, LogMode.LIST );
	}
	
	public String generateLog(String uri, LogMode logMode) throws IOException {
		// parse log
		String log = "";
		if ( fileMode == FileMode.MXML ) {
			log = logsToCSV();
		} else {
			log = logsToCSV();
		}
		
		
		// create file
//		Writer output = null;
//		File file = new File(uri);
//		if ( !file.exists() )
//			file.createNewFile();
//		output = new BufferedWriter(new FileWriter(file));
//		output.write(log);
//		output.close();

		return log;
	}

	private String logToCSV(String id) {
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

}
