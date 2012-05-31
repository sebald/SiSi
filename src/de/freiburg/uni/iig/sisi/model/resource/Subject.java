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

	@Override
	public String toString() {
		return "[" + getId() + "," + getName() + "]";
	}

	/**
	 * Roles are compatible if the {@link Subject}'s roles are a sublist 
	 * of the {@value otherSubject}'s roles. E.g., the {@link Subject}
	 * has only roles the {@value otherSubject} has also.
	 * 
	 * @param otherSubject
	 * @return true, iff roles are a sublist
	 */
	public boolean hasCompatibleRoles(Subject otherSubject) {
		return otherSubject.getRoles().containsAll(roles);
	}

}
