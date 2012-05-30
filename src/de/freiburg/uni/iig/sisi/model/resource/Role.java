package de.freiburg.uni.iig.sisi.model.resource;

import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.NarratorObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class Role extends NarratorObject {
	
	public static String PROPERTY_ADD_DOMAIN = "Add Domain";
	public static String PROPERTY_ADD_MEMBER = "Add Member";
	
	private HashSet<Subject> members = new HashSet<Subject>();
	private HashSet<Transition> domains = new HashSet<Transition>();
	
	public Role(String id, String name) {
		super(id, name);
	}
	
	public HashSet<Subject> getMembers() {
		return members;
	}
	
	public void addMember(Subject member) {
		this.members.add(member);
		notifyListeners(this, PROPERTY_ADD_MEMBER, member);
	}

	public boolean hasMember(Subject member) {
		return this.members.contains(member);
	}

	public HashSet<Transition> getDomains() {
		return domains;
	}

	public void addDomain(Transition transition) {
		this.domains.add(transition);
		notifyListeners(this, PROPERTY_ADD_DOMAIN, transition);
	}
	
}
