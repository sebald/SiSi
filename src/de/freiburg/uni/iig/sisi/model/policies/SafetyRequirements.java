package de.freiburg.uni.iig.sisi.model.policies;

import java.util.HashMap;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.NarratorObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;

public class SafetyRequirements extends NarratorObject {

	public static String PROPERTY_ADD_DELEGATION = "Add Delegation";
	public static String PROPERTY_ADD_POLICY = "Add Policy";
	
	// one transition can has 1 or more roles for delegation
	private HashMap<Transition, HashSet<Role>> delegations = new HashMap<Transition, HashSet<Role>>();
	private HashMap<String, Policy> policyMap = new HashMap<String, Policy>();

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
	
	public HashMap<String, Policy> getPolicyMap() {
		return policyMap;
	}
	
	public void addPolicy(Policy policy) {
		this.policyMap.put(policy.getId(), policy);
	}
	
}
