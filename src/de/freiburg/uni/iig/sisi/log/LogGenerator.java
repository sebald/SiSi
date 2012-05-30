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
import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;
import de.freiburg.uni.iig.sisi.simulation.SimulationEvent;

public class LogGenerator implements PropertyChangeListener {

	public enum FileMode {
		CSV, MXML
	}

	private FileMode fileMode;
	private String newLine = System.getProperty("line.separator");


	private LinkedList<SimulationEvent> events = new LinkedList<SimulationEvent>();

	public LogGenerator(SimulationEngine se) {
		se.addChangeListener(this);
		this.fileMode = FileMode.CSV;
	}

	public LogGenerator(SimulationEngine se, FileMode fileMode) {
		se.addChangeListener(this);
		this.fileMode = fileMode;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == SimulationEngine.PROPERTY_TRANSITION_FIRED) {
			events.add((SimulationEvent) evt.getNewValue());
		}
	}

	public void generateLog() throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH-mm-ss");
		Date date = new Date();
		generateLog("logs/SiSiLog_"+ dateFormat.format(date)  + ".log");
	}

	public void generateLog(String uri) throws IOException {
		// parse log		
		String log = logToCSV();
		
		// create file
		Writer output = null;
		File file = new File(uri);
		if ( !file.exists() )
			file.createNewFile();
		output = new BufferedWriter(new FileWriter(file));
		output.write(log);
		output.close();

	}

	private String logToCSV() {
		String log = "";
		for (SimulationEvent event : events) {
			log += event.toCSV() + System.getProperty("line.separator");
		}
		return log;
	}

}
