package de.freiburg.uni.iig.sisi.model.resource;

import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.model.ModelObject;

public class Subject extends ModelObject {

	private LinkedList<Role> roles = new LinkedList<>();
	
	public Subject(String id, String name) {
		super(id, name);
	}

	public LinkedList<Role> getRoles() {
		return roles;
	}

	public void addRole(Role role) {
		this.roles.add(role);
	}

}
