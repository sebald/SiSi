package de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;

import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy.PolicyType;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.SafetyRequirements;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl.UsageControlType;

public class MutantFactory {
	
	// counter to create ids for the mutants
	private static int created = 0;

	/**
	 * Creates a mutant that violates an authorization and/or delegation.
	 * 
	 * @param processModel
	 * @return
	 */
	public static MutantObject createMutantFor(ProcessModel processModel) {
		HashSet<Transition> transitions = new HashSet<Transition>(processModel.getNet().getTransitions());
		SafetyRequirements sr = processModel.getSafetyRequirements();
		for (Transition transition : processModel.getNet().getTransitions()) {
			if( sr.isPartOf(transition, PolicyType.BINDING_OF_DUTY) || sr.isPartOf(transition, PolicyType.CONFLICT_OF_INTEREST) || sr.isPartOf(transition, UsageControlType.ACTION_REQUIREMENT) ) {
				transitions.remove(transition);
			}
		}
		Random generator = new Random();
		Object[] values = transitions.toArray();		
		return new AuthorizationMutant(createID(), (Transition) values[generator.nextInt(values.length)], processModel);
	}	
	
	/**
	 * Creates a mutant that violates a authorization and/or delegation.
	 * 
	 * @param transition
	 * @return mutant for {@link Transition}
	 */
	public static MutantObject createMutantFor(Transition transition, ProcessModel processModel) {
		return new AuthorizationMutant(createID(), transition, processModel);
	}
	
	/**
	 * Creates a mutant that violates a SoD, BoD or CoI.
	 * 
	 * @param policy
	 * @return mutant for {@link Policy}
	 */
	public static MutantObject createMutantFor(Policy policy, ProcessModel processModel) {
		return new PolicyMutant(createID(), policy, processModel);
	}
	
	/**
	 * Creates a mutant that violates an action requirement or an usage restriction.
	 * 
	 * @param usageControl
	 * @return mutant for {@link UsageControl}
	 */
	public static MutantObject createMutantFor(UsageControl usageControl, ProcessModel processModel) {
		return new UsageControlMutant(createID(), usageControl, processModel);
	}
	
	private static String createID(){
		created++;
		DecimalFormat df = new DecimalFormat("#00");
		return "m"+df.format(created);
	}
	
}
