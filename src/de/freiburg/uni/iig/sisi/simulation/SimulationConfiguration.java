package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashMap;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;

public class SimulationConfiguration {

	public enum ResourceSelectionMode {
		LIST, RANDOM
	}
	
	private final ResourceSelectionMode resourceSelectionMode;
	private final boolean considerSafetyRequirements;

	private HashSet<MutantObject> mutants = new HashSet<MutantObject>();
	
	private HashMap<ModelObject, HashSet<MutantObject>> activatorMap = new HashMap<ModelObject, HashSet<MutantObject>>();
	
	public SimulationConfiguration(ResourceSelectionMode resourceSelectionMode, boolean considerSafetyRequirements) {
		this.resourceSelectionMode = resourceSelectionMode;
		this.considerSafetyRequirements = considerSafetyRequirements;
	}
	
		
	public ResourceSelectionMode getResourceSelectionMode() {
		return resourceSelectionMode;
	}

	public boolean isConsiderSafetyRequirements() {
		return considerSafetyRequirements;
	}


	public HashSet<MutantObject> getMutants() {
		return mutants;
	}

	public void addMutant(MutantObject mutant) {
		this.mutants.add(mutant);
		if ( this.activatorMap.containsKey(mutant.getActivator()) ){
			this.activatorMap.get(mutant.getActivator()).add(mutant);
		} else {
			HashSet<MutantObject> mutantSet = new HashSet<MutantObject>();
			mutantSet.add(mutant);
			this.activatorMap.put(mutant.getActivator(), mutantSet);
		}		
	}

	public HashMap<ModelObject, HashSet<MutantObject>> getActivatorMap() {
		return activatorMap;
	}
	
	public boolean isActivator(ModelObject modelObject){
		return activatorMap.containsKey(modelObject);
	}
	
}
