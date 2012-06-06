package de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;

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

	@Override
	public String toString() {
		String nl = System.lineSeparator();
		String mutation = "";
		String type = "";
		
		if( mutant instanceof AuthorizationMutant ) {
			mutation = "Executed by unauthorized Subject.";
		} else if ( mutant instanceof PolicyMutant ) {
			type = ((Policy) mutant.getActivator()).getType().toString();
			switch (type) {
			case "SoD":
				mutation = "Executed by the same Subject.";
				break;
			case "BoD":
				mutation = "Executed by different Subjects.";
				break;				
			default:
				mutation = "Executed by a Subject that causes a conflict of interest.";
				break;
			}
		} else if ( mutant instanceof UsageControlMutant ) {
			type = ((UsageControl) mutant.getActivator()).getType().toString();
			switch (type) {
			case "AR":
				mutation = "Executed by different Subjects.";
				break;
			default:
				mutation = "Executed by the same Subject.";
				break;
			}
		}
		
		String s = "RunID: " + simulationID + nl + "Type: " + mutant.getClass().getSimpleName();
		if( type != "" )
			s += " (" + type + ")";
		s += nl + "Violated: " + objectViolated + nl + "Mutation: " + mutation;
		
		return s;
	}
	
}
