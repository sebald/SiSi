package de.freiburg.uni.iig.sisi.model;

import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.resource.Subject;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.SafetyRequirements;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;

/**
 * Abstract class for creating {@link MutantObject}. The method {@code createMutation}
 * should be used to create the mutation. E.g. the {@link Subject} set that causes the
 * violation for {@link SafetyRequirements}.
 * Depending on the type of mutant the {@code createMutation} should be called in the
 * {@code Constructor} (static mutant) or in the {@code getMutation} method (dynamic
 * mutant).
 * 
 * @author Sebastian
 *
 */
public abstract class MutantObject extends ModelObject {

	private final ModelObject activator;
	private final ProcessModel processModel;
	protected HashSet<Subject> mutation;
	
	/**
	 * Creates an mutant for the {@link ModelObject}. 
	 * 
	 * @param id
	 * @param activator
	 * @param processModel
	 */
	public MutantObject(String id, ModelObject activator, ProcessModel processModel) {
		super(id, "Mutant for " + activator.getId());
		this.activator = activator;
		this.processModel = processModel;
	}
	
	/**
	 * The activator is a reference for the {@link SimulationEngine} when to consider
	 * the {@link MutantObject}. E.g. when to execute the mutation.
	 * 
	 * @return {@link ModelObject} when the {@link MutantObject} should be activated
	 */
	public ModelObject getActivator() {
		return activator;
	}

	protected ProcessModel getProcessModel() {
		return processModel;
	}

	/**
	 * Getter used by static mutants. Returns {@value mutation}
	 * per default.
	 * 
	 * @return mutation
	 */
	protected HashSet<Subject> getMutation() {
		return this.mutation;
	}
	
	/**
	 * Abstract getter method used by dynamic mutants. Static mutants don't have to
	 * implement this method.
	 * 
	 * @param object some dynamic content that is used to generate the mutation
	 */
	protected abstract HashSet<Subject> getMutation(Object object);

	/**
	 * Created the mutation. E.g. the property that will violates the
	 * {@link SafetyRequirements} when the {@link ProcessModel} is executed.
	 * 
	 * @param modelObject
	 * @return
	 */
	protected abstract HashSet<Subject> createMutation(ModelObject modelObject);
	
}
