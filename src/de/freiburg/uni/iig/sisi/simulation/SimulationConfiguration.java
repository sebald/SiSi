package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.MutantObject;

public class SimulationConfiguration {

	public enum ResourceSelectionMode {
		LIST, RANDOM
	}
	
	private final ResourceSelectionMode resourceSelectionMode;
	private final boolean considerSafetyRequirements;

	private HashSet<MutantObject> mutants = new HashSet<MutantObject>();
	
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
	}
	
}
