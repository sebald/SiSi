package de.freiburg.uni.iig.sisi.simulation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

import de.freiburg.uni.iig.sisi.log.MutationEvent;
import de.freiburg.uni.iig.sisi.log.ProcessInstanceInformation;
import de.freiburg.uni.iig.sisi.log.SimulationEvent;
import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.NarratorObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.net.variant.NetDeviation.DeviationType;
import de.freiburg.uni.iig.sisi.model.net.variant.VariantProcessModel;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy.PolicyType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl.UsageControlType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.AuthorizationMutant;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.MutantFactory;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.MutantObject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.PolicyMutant;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.UsageControlMutant;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration.ResourceSelectionMode;

public class SimulationEngine extends NarratorObject {

	public static String PROPERTY_TRANSITION_FIRED = "Transition fired";
	public static String PORPERTY_SIMULATION_COMPLETE = "Simulation complete";
	public static String PORPERTY_SIMULATION_START = "Simulation start";
	public static String PROPERTY_MUTATION_EXECUTED = "Mutation executed";
	
	public enum ModelState {
		VIOLATED, TEMPORALY_VIOLATED, SATISFIED
	}
	
	private final SimulationConfiguration configuration;

	// important vars while simulation
//	private HashSet<Transition> fireableTransitions = new HashSet<Transition>();
	private HashMap<Transition, HashSet<Policy>> policiesToSatisfy = new HashMap<Transition, HashSet<Policy>>();
	private HashMap<Transition, HashSet<UsageControl>> usageControlsToSatisfy = new HashMap<Transition, HashSet<UsageControl>>();
	private HashMap<MutantObject, ModelObject> executedMutants = new HashMap<MutantObject, ModelObject>();
	private HashMap<Transition, SimulationEvent> internalEventMap = new HashMap<Transition, SimulationEvent>();
	
	ArrayList<ProcessModel> processModels = new ArrayList<ProcessModel>();
	ArrayList<MutantObject> mutations = new ArrayList<MutantObject>();
	
	// stores transitions that have to be executed (causes are policies/uc for example)
	private ArrayList<Transition> forceExecution = new ArrayList<Transition>();
	private ArrayList<Transition> executedTransitions = new ArrayList<Transition>();
	
	// vars for multiple runs
	private ProcessModel currentProcessModel;
	private String simulationRunID;
	
	/**
	 * This map could later be used to execute multiple safety requirement violations in one simulation.
	 */
	private HashMap<ModelObject, HashSet<MutantObject>> activatorMap = new HashMap<ModelObject, HashSet<MutantObject>>();

	public SimulationEngine(SimulationConfiguration simulationConfiguration) throws SimulationExcpetion {
		this.configuration = simulationConfiguration;
		initProcessModells();
	}

	protected void setCurrentProcessModel(ProcessModel currentProcessModel) {
		this.currentProcessModel = currentProcessModel;
	}

	/**
	 * Add an mutant to the mutant(s) that should be executed while simulation the current {@link ProcessModel}.
	 * The mutant will also be added to the {@code activatorMap}. This map is used to activate mutants when the corresponding action,
	 * e.g. the action that should be violated, occurs.
	 * An action can be an execution of a {@link Transition}, {@link Policy} or {@link UsageControl}.
	 * 
	 * @param mutant 
	 */
	protected void setMutantToExecute(MutantObject mutant) {
		if ( activatorMap.containsKey(mutant.getActivator()) ){
			activatorMap.get(mutant.getActivator()).add(mutant);
		} else {
			HashSet<MutantObject> mutantSet = new HashSet<MutantObject>();
			mutantSet.add(mutant);
			activatorMap.put(mutant.getActivator(), mutantSet);
		}
	}

	protected HashMap<ModelObject, HashSet<MutantObject>> getActivatorMap(){
		return activatorMap;
	}
	
	protected boolean isActivator(ModelObject modelObject){
		return activatorMap.containsKey(modelObject);
	}
		
	
	public ArrayList<Transition> getForceExecution() {
		return forceExecution;
	}

	public void addForceExecution(Transition transition) {
		this.forceExecution.add(transition);
	}

	public ArrayList<Transition> getExecutedTransitions() {
		return executedTransitions;
	}

	public void addExecutedTransition(Transition executedTransition) {
		this.executedTransitions.add(executedTransition);
	}

	protected void initProcessModells() {
		// # of runs with original model
		if( configuration.getDeviationMap().containsKey(DeviationType.NONE) ) {
			for (int i = 0; i < configuration.getDeviationMap().get(DeviationType.NONE); i++) {
				processModels.add(configuration.getOriginalModel().clone());
			}
		}
		if( configuration.isConsiderSafetyRequirements() ) {
			// # of runs with a skipping deviation
			if( configuration.getDeviationMap().containsKey(DeviationType.SKIPPING) ) {
				for (int i = 0; i < configuration.getDeviationMap().get(DeviationType.SKIPPING); i++) {
					processModels.add(new VariantProcessModel(configuration.getOriginalModel()));
				}			
			}
			// # of runs with a swapping deviation
			if( configuration.getDeviationMap().containsKey(DeviationType.SWAPPING) ) {
				for (int i = 0; i < configuration.getDeviationMap().get(DeviationType.SWAPPING); i++) {
					processModels.add(new VariantProcessModel(configuration.getOriginalModel(), DeviationType.SWAPPING));
				}				
			}
			// # of runs with a AND2XOR deviation
			if( configuration.getDeviationMap().containsKey(DeviationType.AND2XOR) ) {
				for (int i = 0; i < configuration.getDeviationMap().get(DeviationType.AND2XOR); i++) {
					processModels.add(new VariantProcessModel(configuration.getOriginalModel(), DeviationType.AND2XOR));
				}				
			}		
			// # of runs with a XOR2AND deviation
			if( configuration.getDeviationMap().containsKey(DeviationType.XOR2AND) ) {
				for (int i = 0; i < configuration.getDeviationMap().get(DeviationType.XOR2AND); i++) {
					processModels.add(new VariantProcessModel(configuration.getOriginalModel(), DeviationType.XOR2AND));
				}				
			}
		}
	}
	
	/**
	 * Run the Simulation with the {@link SimulationConfiguration} given when the {@link SimulationEngine} was
	 * constructed.
	 * 
	 * @throws SimulationExcpetion
	 */
	public void run() throws SimulationExcpetion {
		// run the same configuration x times
		for (int i = 0; i < configuration.getNumberOfIterations(); i++) {
			run(i);
		}
	}
	
	protected void run(int iterationNumber) throws SimulationExcpetion {
		// for every process Model
		for (int i = 0; i < processModels.size(); i++) {
			// set and initialize process model
			setCurrentProcessModel(processModels.get(i));
			
			// create mutations for this process model
			createMutationsForCurrentModel();
			
			// run without violations
			int j;
			for (j = 0; j < configuration.getRunsWithoutViolations(); j++) {
				// create run id
				DecimalFormat df = new DecimalFormat("#000");
				simulationRunID = df.format(iterationNumber)+"-"+df.format(i)+"-"+df.format(j);
				
				// simulate model with mutation
				simulateCurrentModel();				
			}
			
			// run the process model for every mutation created
			for (int k = 0; k < mutations.size(); k++) {
				setMutantToExecute(mutations.get(k));
				
				// to really execute the mutant, add the objective to the transitions that have to be executed
				if( configuration.isForceViolations() ) {
					if( mutations.get(k) instanceof PolicyMutant  )
						addForceExecution(((Policy) ((PolicyMutant) mutations.get(k)).getActivator()).getObjective());
					if( mutations.get(k) instanceof UsageControlMutant  )
						addForceExecution(((UsageControl) ((UsageControlMutant) mutations.get(k)).getActivator()).getObjective());					
				}
				
				// create run id
				DecimalFormat df = new DecimalFormat("#000");
				simulationRunID = df.format(iterationNumber)+"-"+df.format(i)+"-"+df.format(j+k);
				
				// simulate model with mutation
				simulateCurrentModel();
				activatorMap.clear();
			}
			
		}
	}
	
	/**
	 * Simulation the current set {@link ProcessModel} with the current set {@link MutantObject} once.
	 * 
	 * @return if the generated trace violates safety requirements or satisfies them.
	 * @throws SimulationExcpetion
	 */
	private ModelState simulateCurrentModel() throws SimulationExcpetion{
		// force violation? yes => create reachability set, if not present.
		if( configuration.isForceViolations()  &&  currentProcessModel.getNet().getReachabilitySet() == null)
			currentProcessModel.getNet().generateReachabilitySet();
		
		notifyListeners(this, PORPERTY_SIMULATION_START, simulationRunID);
		reset();
		while (!currentProcessModel.getNet().getFireableTransitions().isEmpty()) {
			fire();
		}
		
		ProcessInstanceInformation processInstanceInformation = new ProcessInstanceInformation(simulationRunID, currentProcessModel.getName());
		if( currentProcessModel instanceof VariantProcessModel )
			processInstanceInformation.setDeviation(((VariantProcessModel) currentProcessModel).getDeviation());
		notifyListeners(this, PORPERTY_SIMULATION_COMPLETE, processInstanceInformation);
		
		return evaluateModel();
	}
	
	/**
	 * Create {@link MutantObject}s for the current {@link ProcessModel}. Which {@link MutantObject} are created
	 * depends on the {@link SimulationConfiguration}.
	 */
	private void createMutationsForCurrentModel(){
		// clear last settigns
		mutations.clear();
		activatorMap.clear();
		
		// create authorization violations
		for (int i = 0; i < configuration.getRunsViolatingAuthorizations(); i++) {
			mutations.add(MutantFactory.createMutantFor(currentProcessModel));
		}
		// create policy/uc mutants
		for (Entry<ModelObject, Integer> entry : configuration.getViolationMap().entrySet()) {
			Integer count = entry.getValue();
			String id = ((ModelObject) entry.getKey()).getId();
			MutantObject mutant;
			// check what requirement should be violated
			if( entry.getKey() instanceof Policy ) {
				mutant = MutantFactory.createMutantFor(currentProcessModel.getSafetyRequirements().getPolicies().get(id), currentProcessModel);
			} else {
				mutant = MutantFactory.createMutantFor(currentProcessModel.getSafetyRequirements().getUsageControls().get(id), currentProcessModel);
			}
			for (int i = 0; i < count; i++) {
				mutations.add(mutant);
			}
		}
	}
	
	public void reset() throws SimulationExcpetion {
		// reset net
		currentProcessModel.getNet().reset();
		
		// clear internal vars
		policiesToSatisfy.clear();
		usageControlsToSatisfy.clear();
		executedMutants.clear();
		internalEventMap.clear();
		executedTransitions.clear();
		forceExecution.clear();
	}

	/**
	 * This method will fire the {@code currentProcessModel} once, so that after each firing
	 * additional informations can be logged. E.g. who fired the transition.
	 * 
	 * @throws SimulationExcpetion
	 */
	private void fire() throws SimulationExcpetion {
		Transition transition;
		// step-wise firing
		if( configuration.isForceViolations() &&  !getActivatorMap().isEmpty() && configuration.isConsiderSafetyRequirements() ) {
			transition = getAllowedFireableTransition();
			currentProcessModel.getNet().fire(transition);
		} else {
			transition = currentProcessModel.getNet().fire();
		}
		addExecutedTransition(transition);
		
		Subject subject = firedby(transition);
		// generate event
		SimulationEvent event = new SimulationEvent(simulationRunID, transition, subject, currentProcessModel.getResourceModel().getWorkObjectFor(transition));
		internalEventMap.put(transition, event);
		// the event is observable, if the transition has a label (no silent transition)
		if ( !transition.isSilent() )
			notifyListeners(this, PROPERTY_TRANSITION_FIRED, event);		
	}

	private Transition getAllowedFireableTransition() {
		HashSet<Transition> fireableTransitions = currentProcessModel.getNet().getFireableTransitions();
		HashSet<Transition> allowedTransitions = new HashSet<Transition>(currentProcessModel.getNet().getFireableTransitions());		
		// remove transitions that can not lead to the current violation to execute
		
		// check the objectives of safety requirements to check what has to be executed to activate them
		for (Transition transition : fireableTransitions) {
			for (Transition forcedTransition : getForceExecution()) {
				if( getExecutedTransitions().contains(forcedTransition) )
					continue;
				if( !currentProcessModel.getNet().isReachableFrom(transition, forcedTransition) ) {
					allowedTransitions.remove(transition);
					break;
				}
			}
		}
		
		// check the activators what has to be executed
		for (Transition transition : fireableTransitions) {
			for (ModelObject activator : getActivatorMap().keySet()) {
				// authorization violations reachable
				if ( activator instanceof Transition ) {
					// already executed don't bother
					if( getExecutedTransitions().contains((Transition) activator) )
						continue;
					// when the transition is fired the mutation can not be executed (because we can not reach the activator anymore)
					if( !currentProcessModel.getNet().isReachableFrom(transition, (Transition) activator) )
						allowedTransitions.remove(transition);
				// policy violations reachable => eventually part has to be reachable
				} else if ( activator instanceof Policy ) {
					if( getExecutedTransitions().contains(((Policy) activator).getEventually()) )
						continue;
					if( !currentProcessModel.getNet().isReachableFrom(transition, (Transition) ((Policy) activator).getEventually()) )
						allowedTransitions.remove(transition);					
				// uc violations reachable => eventually part has to be reachable
				} else if ( activator instanceof UsageControl ) {
					if( getExecutedTransitions().contains(((UsageControl) activator).getEventually()) )
						continue;
					if( !currentProcessModel.getNet().isReachableFrom(transition, (Transition) ((UsageControl) activator).getEventually()) )
						allowedTransitions.remove(transition);					
				}
			}
		}
		Random generator = new Random();
		Object[] values = allowedTransitions.toArray();
		return (Transition) values[generator.nextInt(values.length)];
	}

	private Subject firedby(Transition transition) throws SimulationExcpetion {
		// this will later be the subjects that fires the transition
		Subject subject = null;
		// get subjects which are authorized (implied through domain assignment)
		HashSet<Subject> subjects = new HashSet<Subject>();
		for (Role role : currentProcessModel.getResourceModel().getDomainFor(transition)) {
			subjects.addAll(role.getMembers());
		}
				
		// check if safetyRquirements should be considered
		if (configuration.isConsiderSafetyRequirements()) {
			
			// execute an authorization mutant?
			if( isActivator(transition) && !transition.isSilent() ) {
				subjects = executeAuthorizationMutant(transition, subjects);
			} else if (currentProcessModel.getSafetyRequirements().hasDelegation(transition)) {
				// get delegations and add subjects that are authorized through the delegations
				HashSet<Role> delegationRoles = currentProcessModel.getSafetyRequirements().getDelegations().get(transition);
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
			if (currentProcessModel.getSafetyRequirements().hasPolicy(transition)) {
				HashSet<Policy> policies = currentProcessModel.getSafetyRequirements().getPolicyMap().get(transition);
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
			if ( currentProcessModel.getSafetyRequirements().hasUsageControl(transition) ) {
				HashSet<UsageControl> uc = currentProcessModel.getSafetyRequirements().getUsageControlMap().get(transition);
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
		if (configuration.getResourceSelectionMode() == ResourceSelectionMode.LIST) {
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
		if( isActivator(policy) ) {
			for (MutantObject mutant : getActivatorMap().get(policy)) {
				if( mutant instanceof PolicyMutant ) {
					executedMutants.put(mutant, policy);
					MutationEvent mutationEvent = new MutationEvent(simulationRunID, mutant, policy, policy.getEventually());
					notifyListeners(this, PROPERTY_MUTATION_EXECUTED, mutationEvent);
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
		
		if( isActivator(usageControl) ) {
			for (MutantObject mutant : getActivatorMap().get(usageControl)) {
				if( mutant instanceof UsageControlMutant ) {
					executedMutants.put(mutant, usageControl);
					MutationEvent mutationEvent = new MutationEvent(simulationRunID, mutant, usageControl, usageControl.getEventually());
					notifyListeners(this, PROPERTY_MUTATION_EXECUTED, mutationEvent);					
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
		for (MutantObject mutant : getActivatorMap().get(transition)) {
			if( mutant instanceof AuthorizationMutant ) {
				executedMutants.put(mutant, transition);
				MutationEvent mutationEvent = new MutationEvent(simulationRunID, mutant, transition, transition);
				notifyListeners(this, PROPERTY_MUTATION_EXECUTED, mutationEvent);					
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
