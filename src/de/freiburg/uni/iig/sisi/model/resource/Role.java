package de.freiburg.uni.iig.sisi.model.resource;

import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.model.NarratorObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class Role extends NarratorObject {
	
	public static String PROPERTY_ADD_DOMAIN = "Add Domain";
	public static String PROPERTY_ADD_MEMBER = "Add Member";
	
	private LinkedList<Subject> members = new LinkedList<Subject>();
	private LinkedList<Transition> domains = new LinkedList<Transition>();
	
	public Role(String id, String name) {
		super(id, name);
	}
	
	public LinkedList<Subject> getMembers() {
		return members;
	}
	
	public void addMember(Subject member) {
		this.members.add(member);
		notifyListeners(this, PROPERTY_ADD_MEMBER, member);
	}

	public boolean hasMember(Subject member) {
		return this.members.contains(member);
	}

	public LinkedList<Transition> getDomains() {
		return domains;
	}

	public void addDomain(Transition transition) {
		this.domains.add(transition);
		notifyListeners(this, PROPERTY_ADD_DOMAIN, transition);
	}
	
}
