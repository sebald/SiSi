package de.freiburg.uni.iig.sisi.model.resource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.net.Transition;

public class ResourceModel implements PropertyChangeListener {

	public HashMap<String, Subject> subjects = new HashMap<String, Subject>();
	public HashMap<String, Role> roles = new HashMap<String, Role>();
	public HashSet<WorkObject> workObjects = new HashSet<WorkObject>();
	
	// maps for quick reference
	public HashMap<Transition, Role> domainMap = new HashMap<Transition, Role>();
	public HashMap<Transition, HashSet<WorkObject>> workObjectsMap = new HashMap<Transition, HashSet<WorkObject>>();
	
	public Collection<Subject> getSubjects() {
		return this.subjects.values();
	}
	
	public void addSubject(Subject subject) {
		this.subjects.put(subject.getId(), subject);
	}
	
	public Subject getSubject(String id) {
		return this.subjects.get(id);
	}
	
	public Collection<Role> getRoles() {
		return this.roles.values();
	}
	
	public void addRole(Role role) {
		this.roles.put(role.getId(), role);
		role.addChangeListener(this);
	}
	
	public Role getRole(String id) {
		return this.roles.get(id);
	}
	
	public HashMap<Transition, Role> getDomainMap() {
		return this.domainMap;
	}

	public Role getDomainFor(Transition transition) {
		return this.domainMap.get(transition);
	}
	
	public void addWorkObject(WorkObject workObject) {
		this.workObjects.add(workObject);
		for (Transition transition : workObject.getTransitions()) {
			if( this.workObjectsMap.containsKey(transition) ) {
				this.workObjectsMap.get(transition).add(workObject);
			} else {
				HashSet<WorkObject> workObjects = new HashSet<WorkObject>();
				workObjects.add(workObject);
				this.workObjectsMap.put(transition, workObjects);
			}
		}
	}
	
	public HashSet<WorkObject> getWorkObjects() {
		return workObjects;
	}

	public HashSet<WorkObject> getWorkObjectFor(Transition transition) {
		return workObjectsMap.get(transition);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if( evt.getPropertyName() == Role.PROPERTY_ADD_DOMAIN )
			domainMap.put((Transition) evt.getNewValue(), (Role) evt.getSource());
	}
	
}
