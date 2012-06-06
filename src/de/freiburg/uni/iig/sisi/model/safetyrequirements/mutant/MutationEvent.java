package de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class MutationEvent {

	private final String simulationID;
	private final MutantObject mutant;
	private final ModelObject objectViolated;
	private final Transition mutatedTransition;
	
	public MutationEvent(String simulationID, MutantObject mutant, ModelObject objectViolated, Transition mutatedTransition) {
		this.simulationID = simulationID;
		this.mutant = mutant;
		this.objectViolated = objectViolated;
		this.mutatedTransition = mutatedTransition;
	}

	public String getSimulationID() {
		return simulationID;
	}

	public MutantObject getMutant() {
		return mutant;
	}

	public ModelObject getObjectViolated() {
		return objectViolated;
	}

	public Transition getMutatedTransition() {
		return mutatedTransition;
	}
	
}
