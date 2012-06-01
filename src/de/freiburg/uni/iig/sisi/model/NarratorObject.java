package de.freiburg.uni.iig.sisi.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * NarratorObject extends {@link ModelObject} with {@link PropertyChangeListener}.
 * @author Sebastian
 *
 */

public class NarratorObject extends ModelObject {
	
	private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	
	public NarratorObject(){
		super();
	}
	
	public NarratorObject(String id, String name) {
		super(id, name);
	}
	
	protected void notifyListeners(Object source, String propertyName, Object value) {
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(new PropertyChangeEvent(source, propertyName, null, value));			
		}

	}
	public void addChangeListener(PropertyChangeListener newListener) {
		listeners.add(newListener);
	}	
	

}
