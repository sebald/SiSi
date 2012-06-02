package de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant;

import java.text.DecimalFormat;

import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;

public class MutantFactory {
	
	// counter to create ids for the mutants
	private static int created = 0;
	
	/**
	 * Creates a mutant that violates a authorization and/or delegation.
	 * 
	 * @param transition
	 * @return mutant for {@link Transition}
	 */
	public static MutantObject createMutantFrom(Transition transition, ProcessModel processModel) {
		return new AuthorizationMutant(createID(), transition, processModel);
	}
	
	/**
	 * Creates a mutant that violates a SoD, BoD or CoI.
	 * 
	 * @param policy
	 * @return mutant for {@link Policy}
	 */
	public static MutantObject createMutantFrom(Policy policy, ProcessModel processModel) {
		return new PolicyMutant(createID(), policy, processModel);
	}
	
	/**
	 * Creates a mutant that violates an action requirement or an usage restriction.
	 * 
	 * @param usageControl
	 * @return mutant for {@link UsageControl}
	 */
	public static MutantObject createMutantFrom(UsageControl usageControl) {
		return null;
	}
	
	private static String createID(){
		created++;
		DecimalFormat df = new DecimalFormat("#00");
		return "m"+df.format(created);
	}
	
}
