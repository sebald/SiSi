package de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant;

import java.util.ArrayList;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Role;
import de.freiburg.uni.iig.sisi.model.resource.Subject;

public class AuthorizationMutant extends MutantObject {

	public AuthorizationMutant(String id, ModelObject activator, ProcessModel processModel) {
		super(id, activator, processModel);
		this.mutation = createMutation(activator);
	}

	@Override
	protected HashSet<Subject> createMutation(ModelObject modelObject) {
		// get subjects that are authorized to execute transition + delegation rules
		HashSet<Subject> authorizedSubjects = new HashSet<Subject>();
		for (Role role : getProcessModel().getResourceModel().getDomainFor((Transition) modelObject)) {
			authorizedSubjects.addAll(role.getMembers());
		}
		HashSet<Role> roleSet = getProcessModel().getSafetyRequirements().getDelegations().get(modelObject);
		if( roleSet != null ) {
			for (Role role : roleSet) {
				authorizedSubjects.addAll(role.getMembers());
			}
		}
		// remove authorized subjects from the new list of subjects that can execute the transition
		ArrayList<Subject> allSubjects = new ArrayList<Subject>(getProcessModel().getResourceModel().getSubjects());
		allSubjects.removeAll(authorizedSubjects);
		// create an HashSet to return
		HashSet<Subject> returnSet = new HashSet<Subject>(allSubjects);		
		return returnSet;
	}
	
	public HashSet<Subject> getMutation(){
		return super.getMutation();
	}

	@Override
	protected HashSet<Subject> getMutation(Object o) {
		return null;
	}

}
