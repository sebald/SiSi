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

public class SimulationEngine extends NarratorObject {

	public static String PROPERTY_TRANSITION_FIRED = "Transition fired";

	public enum ResourceSelectionMode {
		LIST, RANDOM
	}

	// engine config vars
	private final ResourceSelectionMode resourceSelectionMode;
	private boolean respectSafetyRequirements;
	private final SimulationModel simulationModel;

	// important vars while simulation
	private HashMap<String, Transition> fireableTransitions = new HashMap<String, Transition>();
	private HashMap<Transition, Policy> policiesToSatisfy = new HashMap<Transition, Policy>();
	private HashMap<Transition, SimulationEvent> internalEventMap = new HashMap<Transition, SimulationEvent>();

	public SimulationEngine(SimulationModel simulationModel) {
		this(simulationModel, ResourceSelectionMode.RANDOM);
	}

	public SimulationEngine(SimulationModel simulationModel, ResourceSelectionMode mode) {
		this.simulationModel = simulationModel;
		this.resourceSelectionMode = mode;
		this.init();
	}

	private void init() {
		// should the engine respect safety requirements?
		respectSafetyRequirements = true;

		// init fireable transitions
		updateFireableTransitions();
	}

	private void updateFireableTransitions() {
		HashMap<String, Transition> fireableTransitions = new HashMap<String, Transition>();
		for (Transition transition : simulationModel.getNet().getTransitions()) {
			if (transition.isFireable())
				fireableTransitions.put(transition.getId(), transition);
		}
		this.fireableTransitions = fireableTransitions;
	}

	private Transition getRandomFireableTransition() {
		Random generator = new Random();
		Object[] values = fireableTransitions.values().toArray();
		return (Transition) values[generator.nextInt(values.length)];
	}

	public void run() {
		while (!fireableTransitions.isEmpty()) {
			Transition transition = getRandomFireableTransition();

			// internal (not observable operations)
			fire(transition);
			Subject subject = firedby(transition);

			// generate event
			SimulationEvent event = new SimulationEvent(transition, subject, transition.getUsedObject());
			internalEventMap.put(transition, event);
			
			// the event is observable, if the transition has a label (no silent transition)
			if (transition.getName() != null)
				notifyListeners(this, PROPERTY_TRANSITION_FIRED, event);
		}
	}

	private void fire(Transition transition) {
		// we fire it we remove it
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
		// check what is now fireable
		updateFireableTransitions();
	}

	private Subject firedby(Transition transition) {
		Subject subject = null;
		// get subjects which are authorized (implied through domain assignment)
		HashSet<Subject> subjects = simulationModel.getResourceModel().getDomainFor(transition).getMembers();
		
		// check if safetyRquirements should be respected
		if (respectSafetyRequirements) {
			// get delegations and add subjects that are authorized through the
			// delegations
			if (simulationModel.getSafetyRequirements().hasDelegation(transition)) {
				HashSet<Role> delegationRoles = simulationModel.getSafetyRequirements().getDelegations().get(transition);
				for (Role role : delegationRoles) {
					subjects.addAll(role.getMembers());
				}
			}

			// check if transition is an eventually part of one or more policies
			if (policiesToSatisfy.containsKey(transition)) {
				Policy policy = policiesToSatisfy.get(transition);
				SimulationEvent event = internalEventMap.get(policy.getObjective());
				
				// set the available subjects according to the policy rules
				if( policy.getType() == PolicyType.SEPERATION_OF_DUTY ) {
					subjects.remove(event.getSubject());
				} else if ( policy.getType() == PolicyType.BINDING_OF_DUTY  ) {
					subjects = new HashSet<Subject>();
					subjects.add(event.getSubject());
				} else if ( policy.getType() == PolicyType.CONFLICT_OF_INTEREST ) {
					// remove subjects if they have a role, which is not a role of the subject that executed the objective task
					for (Subject subject2 : subjects) {
						if( !subject2.hasCompatibleRoles(event.getSubject()) ) {
							subjects.remove(subject2);
						}
					}
				} else if ( policy.getType() == PolicyType.USAGE_RESTRICTION ) {
					
				} else if ( policy.getType() == PolicyType.ACTION_REQUIREMENT ) {
					
				}
			}

			// check if transition is an objective of one or more policies
			if (simulationModel.getSafetyRequirements().hasPolicy(transition)) {
				HashSet<Policy> policies = simulationModel.getSafetyRequirements().getPolicyMap().get(transition);
				// add policies to policiesToSatisfy map
				for (Policy policy : policies) {
					policiesToSatisfy.put(policy.getEventually(), policy);
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

}
