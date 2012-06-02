package de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant;

import java.util.Collection;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy.PolicyType;
import de.freiburg.uni.iig.sisi.simulation.SimulationEvent;

public class PolicyMutant extends MutantObject {
	
	public PolicyMutant(String id, ModelObject activator, ProcessModel processModel) {
		super(id, activator, processModel);
	}

	@Override
	public HashSet<Subject> createMutation(ModelObject modelObject) {
		// subject = person that executed the objective transition
		Subject subject = ((Subject) modelObject);
		// policy to violate
		Policy policy = (Policy) getActivator();
		// new set of subjects that executes the eventually transition and violate it
		HashSet<Subject> subjects = new HashSet<Subject>();
		
		if ( policy.getType() == PolicyType.SEPERATION_OF_DUTY ) {
			// SOD => BOD = subject that executes is identically with past executed task
			subjects.add(subject);		
		} else if ( policy.getType() == PolicyType.BINDING_OF_DUTY ) {
			// BOD => SOD
			for (Role role : getProcessModel().getResourceModel().getDomainFor(policy.getEventually())) {
				subjects.addAll(role.getMembers());
			}			
			subjects.remove(subject);
		} else if ( policy.getType() == PolicyType.CONFLICT_OF_INTEREST ) {
			// only subjects that don't share a role with the subject that executed the objective transitions
			// but the should be authorized to execute the transition
			Collection<Role> allRoles = getProcessModel().getResourceModel().getDomainFor(policy.getEventually());
			// remove all good roles (= roles that have the subjects as member)
			for (Role role : subject.getRoles()) {
				allRoles.remove(role);
			}
			// get all members of the bad roles (= subjects that cause a CoI if they execute the eventually transition)
			for (Role role : allRoles) {
				subjects.addAll(role.getMembers());
			}
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
