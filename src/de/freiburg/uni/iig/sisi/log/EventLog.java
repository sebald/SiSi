package de.freiburg.uni.iig.sisi.log;

import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.simulation.SimulationEvent;

public class EventLog {

	private LinkedList<SimulationEvent> events = new LinkedList<SimulationEvent>();

	public LinkedList<SimulationEvent> getEvents() {
		return events;
	}

	public void addEvent(SimulationEvent event) {
		events.add(event);
	}
	
}
