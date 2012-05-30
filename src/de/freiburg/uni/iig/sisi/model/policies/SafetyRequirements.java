package de.freiburg.uni.iig.sisi.model.policies;

import java.util.HashMap;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;

public class SafetyRequirements {

	private HashMap<Role, Transition> delegations = new HashMap<>();
	private HashMap<String, Policy> policyMap = new HashMap<>();

	public HashMap<Role, Transition> getDelegations() {
		return delegations;
	}
	
	public void addDelegation(Role role, Transition transition) {
		this.delegations.put(role, transition);
	}
	
	public HashMap<String, Policy> getPolicyMap() {
		return policyMap;
	}
	
	public void addPolicy(Policy policy) {
		this.policyMap.put(policy.getId(), policy);
	}
	
}
