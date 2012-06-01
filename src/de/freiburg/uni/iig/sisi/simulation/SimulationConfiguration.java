package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashMap;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class SimulationConfiguration {

	public enum ResourceSelectionMode {
		LIST, RANDOM
	}
	
	private final ResourceSelectionMode resourceSelectionMode;
	private final boolean considerSafetyRequirements;

	private HashSet<MutantObject> mutants = new HashSet<MutantObject>();
	
	private HashMap<Transition, HashSet<MutantObject>> mutantMap = new HashMap<Transition, HashSet<MutantObject>>();
	
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
		if ( this.mutantMap.containsKey(mutant.getActivator()) ){
			this.mutantMap.get(mutant.getActivator()).add(mutant);
		} else {
			HashSet<MutantObject> mutantSet = new HashSet<MutantObject>();
			mutantSet.add(mutant);
			this.mutantMap.put((Transition) mutant.getActivator(), mutantSet);
		}		
	}

	public HashMap<Transition, HashSet<MutantObject>> getMutantMap() {
		return mutantMap;
	}
	
}
