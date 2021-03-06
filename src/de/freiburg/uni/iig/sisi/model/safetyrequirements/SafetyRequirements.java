package de.freiburg.uni.iig.sisi.model.safetyrequirements;

import java.util.HashMap;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy.PolicyType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl.UsageControlType;

public class SafetyRequirements {
	
	// one transition can has one or more roles for delegation
	private HashMap<Transition, HashSet<Role>> delegations = new HashMap<Transition, HashSet<Role>>();
	private HashMap<String, Policy> policies = new HashMap<String, Policy>();
	private HashMap<String, UsageControl> usageControls = new HashMap<String, UsageControl>();
	
	// maps for quick reference
	private HashMap<Transition, HashSet<Policy>> policyMap = new HashMap<Transition, HashSet<Policy>>();
	private HashMap<Transition, HashSet<UsageControl>> usageControlMap = new HashMap<Transition, HashSet<UsageControl>>();
	private HashSet<Transition> objectiveMap = new HashSet<Transition>();
	private HashSet<Transition> eventuallyMap = new HashSet<Transition>();
	
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
	
	public HashMap<String, Policy> getPolicies() {
		return policies;
	}
	
	public void addPolicy(Policy policy) {
		this.policies.put(policy.getId(), policy);
		if ( this.policyMap.containsKey(policy.getObjective()) ){
			this.policyMap.get(policy.getObjective()).add(policy);
		} else {
			HashSet<Policy> policySet = new HashSet<Policy>();
			policySet.add(policy);
			this.policyMap.put(policy.getObjective(), policySet);
		}
		this.objectiveMap.add(policy.getObjective());
		this.eventuallyMap.add(policy.getEventually());
	}

	public boolean hasDelegation(Transition transition) {
		if  (this.delegations.containsKey(transition) ) return true;
		return false;
	}
	
	public boolean hasPolicy(Transition transition) {
		if  (this.policyMap.containsKey(transition) ) return true;
		return false;		
	}
	
	public HashMap<Transition, HashSet<Policy>> getPolicyMap() {
		return policyMap;
	}

	public HashMap<String, UsageControl> getUsageControls() {
		return usageControls;
	}	
	
	public void addUsageControl(UsageControl usageControl) {
		this.usageControls.put(usageControl.getId(), usageControl);
		if ( this.usageControlMap.containsKey(usageControl.getObjective()) ){
			this.usageControlMap.get(usageControl.getObjective()).add(usageControl);
		} else {
			HashSet<UsageControl> policySet = new HashSet<UsageControl>();
			policySet.add(usageControl);
			this.usageControlMap.put(usageControl.getObjective(), policySet);
		}
		this.objectiveMap.add(usageControl.getObjective());
		this.eventuallyMap.add(usageControl.getEventually());
	}

	public HashMap<Transition, HashSet<UsageControl>> getUsageControlMap() {
		return usageControlMap;
	}
	
	public boolean hasUsageControl(Transition transition) {
		if (this.usageControlMap.containsKey(transition)) return true;
		return false;
	}

	public HashSet<Transition> getEventuallyMap() {
		return eventuallyMap;
	}

	public HashSet<Transition> getObjectiveMap() {
		return objectiveMap;
	}
	
	public boolean isPartOf(Transition transition, PolicyType type) {
		for (Policy policy : policies.values()) {
			if( policy.getType() == type ) {
				if( policy.getObjective() ==  transition) return true;
				if( policy.getEventually() == transition ) return true;		
			}
		}
		return false;
	}
	
	public boolean isPartOf(Transition transition, UsageControlType type) {
		for (UsageControl uc : usageControls.values()) {
			if( uc.getType() == type ) {
				if( uc.getObjective() ==  transition) return true;
				if( uc.getEventually() == transition ) return true;		
			}
		}
		return false;
	}
	
}
