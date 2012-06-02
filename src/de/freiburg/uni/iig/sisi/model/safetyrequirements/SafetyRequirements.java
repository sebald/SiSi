package de.freiburg.uni.iig.sisi.model.safetyrequirements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;

public class SafetyRequirements {
	
	// one transition can has one or more roles for delegation
	private HashMap<Transition, HashSet<Role>> delegations = new HashMap<Transition, HashSet<Role>>();
	private LinkedList<Policy> policies = new LinkedList<Policy>();
	private LinkedList<UsageControl> usageControls = new LinkedList<UsageControl>();
	
	// maps for quick reference
	private HashMap<Transition, HashSet<Policy>> policyMap = new HashMap<Transition, HashSet<Policy>>();
	private HashMap<Transition, HashSet<UsageControl>> usageControlMap = new HashMap<Transition, HashSet<UsageControl>>();
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
	
	public LinkedList<Policy> getPolicies() {
		return policies;
	}
	
	public void addPolicy(Policy policy) {
		this.policies.add(policy);
		if ( this.policyMap.containsKey(policy.getObjective()) ){
			this.policyMap.get(policy.getObjective()).add(policy);
		} else {
			HashSet<Policy> policySet = new HashSet<Policy>();
			policySet.add(policy);
			this.policyMap.put(policy.getObjective(), policySet);
		}
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

	public LinkedList<UsageControl> getUsageControls() {
		return usageControls;
	}	
	
	public void addUsageControl(UsageControl usageControl) {
		this.usageControls.add(usageControl);
		if ( this.usageControlMap.containsKey(usageControl.getObjective()) ){
			this.usageControlMap.get(usageControl.getObjective()).add(usageControl);
		} else {
			HashSet<UsageControl> policySet = new HashSet<UsageControl>();
			policySet.add(usageControl);
			this.usageControlMap.put(usageControl.getObjective(), policySet);
		}
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
	
}
