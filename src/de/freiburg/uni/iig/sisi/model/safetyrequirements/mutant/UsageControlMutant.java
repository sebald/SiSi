package de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant;

import java.util.HashSet;

import de.freiburg.uni.iig.sisi.log.SimulationEvent;
import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl.UsageControlType;

public class UsageControlMutant extends MutantObject {

	public UsageControlMutant(String id, ModelObject activator, ProcessModel processModel) {
		super(id, activator, processModel);
	}

	@Override
	protected HashSet<Subject> createMutation(ModelObject modelObject) {
		// subject = person that executed the objective transition
		Subject subject = ((Subject) modelObject);
		// usage control to violate
		UsageControl uc = (UsageControl) getActivator();
		// new set of subjects that executes the eventually transition and violate it
		HashSet<Subject> subjects = new HashSet<Subject>();
		
		if ( uc.getType() == UsageControlType.USAGE_RESTRICTION ) {
			// UR => AC
			subjects.add(subject);
		} else if ( uc.getType() == UsageControlType.ACTION_REQUIREMENT ) {
			// AC => UR
			for (Role role : getProcessModel().getResourceModel().getDomainFor(uc.getEventually())) {
				subjects.addAll(role.getMembers());
			}			
			subjects.remove(subject);			
		}
		
		return subjects;
	}

	public HashSet<Subject> getMutation(SimulationEvent event) {
		return getMutation(event.getSubject());
	}
	
	
	@Override
	protected HashSet<Subject> getMutation(Object object) {
		return createMutation((Subject) object);
	}
	
	
}
