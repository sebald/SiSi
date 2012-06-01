package de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant;

import java.util.Collection;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;

public class AuthorizationMutant extends MutantObject {

	public AuthorizationMutant(String id, Transition transition, ProcessModel processModel) {
		super(id, transition, processModel);
	}

	@Override
	protected HashSet<Subject> createMutation(ModelObject modelObject) {
		// get subjects that are authorized to execute transition
		HashSet<Subject> authorizedSubjects = getProcessModel().getResourceModel().getDomainFor((Transition) modelObject).getMembers();
		HashSet<Role> roleSet = getProcessModel().getSafetyRequirements().getDelegations().get(modelObject);
		if( roleSet != null ) {
			for (Role role : roleSet) {
				authorizedSubjects.addAll(role.getMembers());
			}
		}
		// remove authorized subjects from the new list of subjects that can execute the transition
		Collection<Subject> allSubjects = getProcessModel().getResourceModel().getSubjects();
		allSubjects.removeAll(authorizedSubjects);
		// create an HashSet to return
		HashSet<Subject> returnSet = new HashSet<Subject>();
		for (Subject subject : allSubjects) {
			returnSet.add(subject);
		}
		
		return returnSet;
	}

}
