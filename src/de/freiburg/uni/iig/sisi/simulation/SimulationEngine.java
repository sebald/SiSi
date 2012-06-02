package de.freiburg.uni.iig.sisi.simulation;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.NarratorObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Arc;
import de.freiburg.uni.iig.sisi.model.net.Place;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy.PolicyType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl.UsageControlType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.AuthorizationMutant;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.PolicyMutant;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.UsageControlMutant;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration.ResourceSelectionMode;

public class SimulationEngine extends NarratorObject {

	public static String PROPERTY_TRANSITION_FIRED = "Transition fired";
	public static String PORPERTY_SIMULATION_COMPLETE = "Simulation complete";
	public static String PORPERTY_SIMULATION_START = "Simulation start";
	
	public enum ModelState {
		VIOLATED, TEMPORALY_VIOLATED, SATISFIED
	}
	
	// engine config vars
	private final ProcessModel processModel;
	private final SimulationConfiguration simulationConfiguration;

	// important vars while simulation
	private HashSet<Transition> fireableTransitions = new HashSet<Transition>();
	private HashMap<Transition, HashSet<Policy>> policiesToSatisfy = new HashMap<Transition, HashSet<Policy>>();
	private HashMap<Transition, HashSet<UsageControl>> usageControlsToSatisfy = new HashMap<Transition, HashSet<UsageControl>>();
	private HashMap<MutantObject, ModelObject> executedMutants = new HashMap<MutantObject, ModelObject>();
	private HashMap<Transition, SimulationEvent> internalEventMap = new HashMap<Transition, SimulationEvent>();
	
	// vars for multiple runs
	private MutantObject mutantToExecute;
	private String simulationRunID;

	public SimulationEngine(ProcessModel simulationModel, SimulationConfiguration simulationConfiguration) throws SimulationExcpetion {
		this.processModel = simulationModel;
		this.simulationConfiguration = simulationConfiguration;
		updateFireableTransitions();
	}
	
	protected MutantObject getMutantToExecute() {
		return mutantToExecute;
	}

	protected void setMutantToExecute(MutantObject mutantToExecute) {
		this.mutantToExecute = mutantToExecute;
	}

	public void run() throws SimulationExcpetion {
		for (int i = 0; i < simulationConfiguration.getMutants().size(); i++) {
			setMutantToExecute(simulationConfiguration.getMutants().get(i));
			
			DecimalFormat df = new DecimalFormat("#000");
			simulationRunID = "sim#"+df.format(i);
			
			notifyListeners(this, PORPERTY_SIMULATION_START, simulationRunID);
			while (!fireableTransitions.isEmpty()) {
				Transition transition = getRandomFireableTransition();
				fire(transition);
			}
			
			reset();
		}
		
		evaluateModel();
	}	
	
	public void reset() throws SimulationExcpetion {
		// reset net
		processModel.getNet().reset();
		updateFireableTransitions();
		
		// notify log
		notifyListeners(this, PORPERTY_SIMULATION_COMPLETE, simulationRunID);
		
		// clear internal vars
		policiesToSatisfy.clear();
		usageControlsToSatisfy.clear();
		executedMutants.clear();
		internalEventMap.clear();
	}
	
	private void updateFireableTransitions() throws SimulationExcpetion {
		HashSet<Transition> fireableTransitions = new HashSet<Transition>();
		// add every transition that could be fired (ignore safety requirements for now)
		for (Transition transition : processModel.getNet().getTransitions()) {
			if (transition.isFireable()) {				
				fireableTransitions.add(transition);
			}
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
		
		Subject subject = firedby(transition);
		// generate event
		SimulationEvent event = new SimulationEvent(simulationRunID, transition, subject, processModel.getResourceModel().getWorkObjectFor(transition));
		internalEventMap.put(transition, event);
		// the event is observable, if the transition has a label (no silent transition)
		if (transition.getName() != null)
			notifyListeners(this, PROPERTY_TRANSITION_FIRED, event);		
		
		// check what is now fireable
		updateFireableTransitions();
	}

	private Subject firedby(Transition transition) throws SimulationExcpetion {
		// this will later be the subjects that fires the transition
		Subject subject = null;
		// get subjects which are authorized (implied through domain assignment)
		HashSet<Subject> subjects = new HashSet<Subject>();
		for (Role role : processModel.getResourceModel().getDomainFor(transition)) {
			subjects.addAll(role.getMembers());
		}
		
		// check if safetyRquirements should be considered
		if (simulationConfiguration.isConsiderSafetyRequirements()) {
			
			if( simulationConfiguration.isActivator(transition) && !transition.isSilent() ) {
				subjects = executeAuthorizationMutant(transition, subjects);
			} else if (processModel.getSafetyRequirements().hasDelegation(transition)) {
				// get delegations and add subjects that are authorized through the delegations
				HashSet<Role> delegationRoles = processModel.getSafetyRequirements().getDelegations().get(transition);
				for (Role role : delegationRoles) {
					subjects.addAll(role.getMembers());
				}
			}

			/**
			 * 		Eventually part of an policy/usage control found that has to be satisfied.
			 */
			
			// check if transition is an eventually part of one or more policies/usage controls
			if (policiesToSatisfy.containsKey(transition)) {
				HashSet<Policy> policies = policiesToSatisfy.get(transition);
				for (Policy policy : policies) {
					subjects = satisfyPolicy(policy, subjects);
					policiesToSatisfy.get(transition).remove(policy);
					if( policiesToSatisfy.get(transition).isEmpty() )
						policiesToSatisfy.remove(transition);
				}
			}
			if (usageControlsToSatisfy.containsKey(transition)) {
				HashSet<UsageControl> usageControls = usageControlsToSatisfy.get(transition);
				for (UsageControl usageControl : usageControls) {
					subjects = satisfyUsageControl(usageControl, subjects);
					usageControlsToSatisfy.get(transition).remove(usageControl);
					if( usageControlsToSatisfy.get(transition).isEmpty() )
						usageControlsToSatisfy.remove(transition);
				}
			}			
			
			/**
			 * 		Objective of a policy/usage control spotted.
			 */
			
			// check if transition is an objective of one or more policies/usage controls
			if (processModel.getSafetyRequirements().hasPolicy(transition)) {
				HashSet<Policy> policies = processModel.getSafetyRequirements().getPolicyMap().get(transition);
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
			if ( processModel.getSafetyRequirements().hasUsageControl(transition) ) {
				HashSet<UsageControl> uc = processModel.getSafetyRequirements().getUsageControlMap().get(transition);
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

		}

		// resource selection
		if (simulationConfiguration.getResourceSelectionMode() == ResourceSelectionMode.LIST) {
			// first
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
		
		// check if we rather should violate the policy than satisfy it
		if( simulationConfiguration.isActivator(policy) ) {
			for (MutantObject mutant : simulationConfiguration.getActivatorMap().get(policy)) {
				if( mutant instanceof PolicyMutant ) {
					executedMutants.put(mutant, policy);
					subjectSet = ((PolicyMutant) mutant).getMutation(event);
				}
			}
		} else {
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
		}
		
		if ( subjectSet.isEmpty() )
			throw new SimulationExcpetion(policy);
		
		return subjectSet;
	}

	private HashSet<Subject> satisfyUsageControl(UsageControl usageControl, HashSet<Subject> subjectSet) throws SimulationExcpetion {
		// get event to check who has executed the task
		SimulationEvent event = internalEventMap.get(usageControl.getObjective());
		
		if( simulationConfiguration.isActivator(usageControl) ) {
			for (MutantObject mutant : simulationConfiguration.getActivatorMap().get(usageControl)) {
				if( mutant instanceof UsageControlMutant ) {
					executedMutants.put(mutant, usageControl);
					subjectSet = ((UsageControlMutant) mutant).getMutation(event);
				}
			}			
		} else {
			// set the available subjects according to the policy rules
			if( usageControl.getType() == UsageControlType.USAGE_RESTRICTION ) {
				subjectSet.remove(event.getSubject());
			} else if ( usageControl.getType() == UsageControlType.ACTION_REQUIREMENT ) {
				subjectSet = new HashSet<Subject>();
				subjectSet.add(event.getSubject());
			}
		}
		
		if ( subjectSet.isEmpty() )
			throw new SimulationExcpetion(usageControl);
		
		return subjectSet;
	}

	private HashSet<Subject> executeAuthorizationMutant(Transition transition, HashSet<Subject> subjects) {
		for (MutantObject mutant : simulationConfiguration.getActivatorMap().get(transition)) {
			if( mutant instanceof AuthorizationMutant ) {
				executedMutants.put(mutant, transition);
				return ((AuthorizationMutant) mutant).getMutation();
			}
		}
		return subjects;
	}	
	
	private ModelState evaluateModel(){
		if( policiesToSatisfy.isEmpty() && usageControlsToSatisfy.isEmpty() && executedMutants.isEmpty() )
			return ModelState.SATISFIED;
		
		return ModelState.VIOLATED;
	}
	
}
