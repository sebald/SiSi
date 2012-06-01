package de.freiburg.uni.iig.sisi.model;

import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.SafetyRequirements;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;

public abstract class MutantObject extends ModelObject {

	private final ModelObject forObject;
	private final ProcessModel processModel;
	private final HashSet<Subject> mutation;
	
	public MutantObject(String id, ModelObject modelObject, ProcessModel processModel) {
		super(id, "Mutant for " + modelObject.getId());
		this.forObject = modelObject;
		this.processModel = processModel;
		this.mutation = createMutation(modelObject);
	}
	
	public ModelObject forModelObject(){
		return this.forObject;
	}

	public ModelObject getForObject() {
		return forObject;
	}

	protected ProcessModel getProcessModel() {
		return processModel;
	}

	public HashSet<Subject> getMutation() {
		return mutation;
	}

	/**
	 * Created the mutation. E.g. the property that will violates the
	 * {@link SafetyRequirements} when the {@link ProcessModel} is executed.
	 * 
	 * @param modelObject
	 * @return
	 */
	protected abstract HashSet<Subject> createMutation(ModelObject modelObject);
	
	/**
	 * The activator is a reference for the {@link SimulationEngine} when to consider
	 * the {@link MutantObject}. E.g. when to execute the mutation.
	 * 
	 * @return {@link Transition} when the {@link MutantObject} should be executed
	 */
	public abstract ModelObject getActivator();
	
}
