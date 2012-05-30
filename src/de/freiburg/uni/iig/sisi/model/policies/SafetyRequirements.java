package de.freiburg.uni.iig.sisi.model.policies;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;

public class SafetyRequirements {
	
	// one transition can has one or more roles for delegation
	private HashMap<Transition, HashSet<Role>> delegations = new HashMap<Transition, HashSet<Role>>();
	private LinkedList<Policy> policies= new LinkedList<Policy>();
	
	// maps for quick reference
	private HashSet<Transition> policyMap = new HashSet<Transition>();

	public HashMap<Transition, HashSet<Role>> getDelegations() {
		return delegations;
	}
	
	public void addDelegation(Transition transition, Role role) {
		if ( this.delegations.containsKey(transition) ){
			this.delegations.get(transition).add(role);
		} else {
			HashSet<Role> roleSet = new HashSet<Role>();
			roleSet.add(role);
			this.delegations.put(transition, roleSet);
		}
	}
	
	public LinkedList<Policy> getPolicies() {
		return policies;
	}
	
	public void addPolicy(Policy policy) {
		this.policies.add(policy);
		this.policyMap.add(policy.getEventually());
	}
	
	public boolean hasDelegation(Transition transition) {
		if  (delegations.containsKey(transition) ) return true;
		return false;
	}
	
	public boolean hasPolicy(Transition transition) {
		if  (policyMap.contains(transition) ) return true;
		return false;		
	}
	
}
