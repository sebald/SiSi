package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import de.freiburg.uni.iig.sisi.model.NarratorObject;
import de.freiburg.uni.iig.sisi.model.net.Arc;
import de.freiburg.uni.iig.sisi.model.net.Place;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy.PolicyType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl.UsageControlType;

public class SimulationEngine extends NarratorObject {

	public static String PROPERTY_TRANSITION_FIRED = "Transition fired";

	public enum ResourceSelectionMode {
		LIST, RANDOM
	}

	// engine config vars
	private final ResourceSelectionMode resourceSelectionMode;
	private boolean considerSafetyRequirements;
	private final SimulationModel simulationModel;

	// important vars while simulation
	private HashSet<Transition> fireableTransitions = new HashSet<Transition>();
	private HashMap<Transition, HashSet<Policy>> policiesToSatisfy = new HashMap<Transition, HashSet<Policy>>();
	private HashMap<Transition, HashSet<UsageControl>> usageControlsToSatisfy = new HashMap<Transition, HashSet<UsageControl>>();
	private HashMap<Transition, SimulationEvent> internalEventMap = new HashMap<Transition, SimulationEvent>();

	public SimulationEngine(SimulationModel simulationModel) throws SimulationExcpetion {
		this(simulationModel, ResourceSelectionMode.RANDOM);
	}

	public SimulationEngine(SimulationModel simulationModel, ResourceSelectionMode mode) throws SimulationExcpetion {
		this.simulationModel = simulationModel;
		this.resourceSelectionMode = mode;
		this.init();
	}

	private void init() throws SimulationExcpetion {
		// should the engine respect safety requirements?
		considerSafetyRequirements = true;

		// init fireable transitions
		updateFireableTransitions();
	}

	public void run() throws SimulationExcpetion {
		while (!fireableTransitions.isEmpty()) {
			Transition transition = getRandomFireableTransition();
			// internal (not observable operations)
			fire(transition);
			Subject subject = firedby(transition);
			// generate event
			SimulationEvent event = new SimulationEvent(transition, subject, simulationModel.getResourceModel().getWorkObjectFor(transition));
			internalEventMap.put(transition, event);
			// the event is observable, if the transition has a label (no silent transition)
			if (transition.getName() != null)
				notifyListeners(this, PROPERTY_TRANSITION_FIRED, event);
		}
	}	
	
	private void updateFireableTransitions() throws SimulationExcpetion {
		HashSet<Transition> fireableTransitions = new HashSet<Transition>();
		// add every transition that could be fired (ignore safety requirements for now)
		for (Transition transition : simulationModel.getNet().getTransitions()) {
			if (transition.isFireable()) {				
				fireableTransitions.add(transition);
			}
		}
		// adapt the fireable set according to usage control rules
		if( considerSafetyRequirements && (fireableTransitions.size() > 0) ) {
					fireableTransitions = satisfyUsageControl(fireableTransitions);
		}
		this.fireableTransitions = fireableTransitions;
	}

	private Transition getRandomFireableTransition() {
		Random generator = new Random();
		Object[] values = fireableTransitions.toArray();
		return (Transition) values[generator.nextInt(values.length)];
	}

	private void fire(Transition transition) throws SimulationExcpetion {
		// we fire it, we remove it
		fireableTransitions.remove(transition.getId());
		// remove tokens form pre set
		for (Arc arc : transition.getIncomingArcs()) {
			Place p = ((Place) arc.getSource());
			p.setMarking(p.getMarking() - 1);
		}
		// add tokens to post set
		for (Arc arc : transition.getOutgoingArcs()) {
			Place p = ((Place) arc.getTarget());
			p.setMarking(p.getMarking() + 1);
		}
		// is the fired transition part of a usage control rule?
		if ( considerSafetyRequirements && simulationModel.getSafetyRequirements().hasUsageControl(transition) ) {
			HashSet<UsageControl> uc = simulationModel.getSafetyRequirements().getUsageControlMap().get(transition);
			for (UsageControl usageControl : uc) {
				// is eventually-transition already registers?
				if( usageControlsToSatisfy.containsKey(usageControl.getEventually()) ) {
					usageControlsToSatisfy.get(usageControl.getEventually()).add(usageControl);
				} else {
					HashSet<UsageControl> newusageControls = new HashSet<UsageControl>();
					newusageControls.add(usageControl);
					usageControlsToSatisfy.put(usageControl.getEventually(), newusageControls);
				}
			}
		}
		// is the fire transition the eventually part of an action requirement?
		if ( considerSafetyRequirements && usageControlsToSatisfy.containsKey(transition) ) {
			for (UsageControl usageControl : usageControlsToSatisfy.get(transition)) {
				// if so => remove it, b/c it is satisfied
				if( usageControl.getType() == UsageControlType.ACTION_REQUIREMENT )
					usageControlsToSatisfy.get(transition).remove(usageControl);
				if ( usageControlsToSatisfy.get(transition).isEmpty() )
					usageControlsToSatisfy.remove(transition);
			}
			
		}
		
		// check what is now fireable
		updateFireableTransitions();
	}

	private Subject firedby(Transition transition) throws SimulationExcpetion {
		Subject subject = null;
		// get subjects which are authorized (implied through domain assignment)
		HashSet<Subject> subjects = simulationModel.getResourceModel().getDomainFor(transition).getMembers();
		
		// check if safetyRquirements should be considered
		if (considerSafetyRequirements) {
			// get delegations and add subjects that are authorized through the delegations
			if (simulationModel.getSafetyRequirements().hasDelegation(transition)) {
				HashSet<Role> delegationRoles = simulationModel.getSafetyRequirements().getDelegations().get(transition);
				for (Role role : delegationRoles) {
					subjects.addAll(role.getMembers());
				}
			}

			// check if transition is an eventually part of one or more policies
			if (policiesToSatisfy.containsKey(transition)) {
				HashSet<Policy> policies = policiesToSatisfy.get(transition);
				for (Policy policy : policies) {
					subjects = satisfyPolicy(policy, subjects);
				}
			}

			// check if transition is an objective of one or more policies
			if (simulationModel.getSafetyRequirements().hasPolicy(transition)) {
				HashSet<Policy> policies = simulationModel.getSafetyRequirements().getPolicyMap().get(transition);
				// add policies to policiesToSatisfy map
				for (Policy policy : policies) {
					// is eventually-transition already registers?
					if( policiesToSatisfy.containsKey(policy.getEventually()) ) {
						policiesToSatisfy.get(policy.getEventually()).add(policy);
					} else {
						HashSet<Policy> newPolicies = new HashSet<Policy>();
						newPolicies.add(policy);
						policiesToSatisfy.put(policy.getEventually(), newPolicies);
					}
				}
			}

		}

		// resource selection
		if (resourceSelectionMode == ResourceSelectionMode.LIST) {
			subject = subjects.iterator().next();
		} else {
			// random
			Random generator = new Random();
			Object[] values = subjects.toArray();
			subject = (Subject) values[generator.nextInt(values.length)];
		}
		return subject;
	}
	
	private HashSet<Subject> satisfyPolicy(Policy policy, HashSet<Subject> subjectSet) throws SimulationExcpetion {
		// get event to check who has executed the task
		SimulationEvent event = internalEventMap.get(policy.getObjective());
		// set the available subjects according to the policy rules
		if( policy.getType() == PolicyType.SEPERATION_OF_DUTY ) {
			subjectSet.remove(event.getSubject());
		} else if ( policy.getType() == PolicyType.BINDING_OF_DUTY  ) {
			subjectSet = new HashSet<Subject>();
			subjectSet.add(event.getSubject());
		} else if ( policy.getType() == PolicyType.CONFLICT_OF_INTEREST ) {
			// remove subjects if they have a role, which is not a role of the subject that executed the objective task
			for (Subject subject : subjectSet) {
				if( !subject.hasCompatibleRoles(event.getSubject()) ) {
					subjectSet.remove(subject);
				}
			}
		}
		
		if ( subjectSet.isEmpty() )
			throw new SimulationExcpetion(policy);
		
		policiesToSatisfy.remove(policy);
		
		return subjectSet;
	}

	private HashSet<Transition> satisfyUsageControl(HashSet<Transition> fireableTransitions) throws SimulationExcpetion {	
		// set of transitions that can only be fired now
		HashSet<Transition> needToBeFired = new HashSet<Transition>();
		
		for (Transition transition : fireableTransitions) {
			if(usageControlsToSatisfy.containsKey(transition)) {
				HashSet<UsageControl> usageControlSet = usageControlsToSatisfy.get(transition);
				
				// check if transition is not part of action requirement AND usage restriction
				boolean isAR = false;
				boolean isUR = false;
				for (UsageControl usageControl : usageControlSet) {
					if( usageControl.getType() == UsageControlType.ACTION_REQUIREMENT )
						isAR = true;
					if( usageControl.getType() == UsageControlType.USAGE_RESTRICTION )
						isUR = true;
				}
				if( isAR && isUR )
					throw new SimulationExcpetion(transition);
				
				for (UsageControl usageControl : usageControlSet) {
					// transition is part of an action requirement and can only be fired now
					if( usageControl.getType() == UsageControlType.ACTION_REQUIREMENT && !transition.canFireLater() ) {
						needToBeFired.add(transition);
					} else if ( usageControl.getType() == UsageControlType.USAGE_RESTRICTION ) {
						fireableTransitions.remove(transition);
					}
				}				
			}
		}
		
		// more than one transitions can only be fired now that are part of an action requirement
		if ( needToBeFired.size() > 1 )
			throw new SimulationExcpetion(needToBeFired);
		// fireableTransitions are empty
		if ( fireableTransitions.isEmpty() )
			throw new SimulationExcpetion(usageControlsToSatisfy);
		
		// there is one transition that has to be fired now
		if ( needToBeFired.size() == 1 )
			fireableTransitions = needToBeFired;

		return fireableTransitions;
	}
	
}
