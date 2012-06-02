package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;

public class SimulationConfiguration {

	public enum ResourceSelectionMode {
		LIST, RANDOM
	}
	
	private final ResourceSelectionMode resourceSelectionMode;
	private final boolean considerSafetyRequirements;

	private LinkedList<ProcessModel> processModels = new LinkedList<ProcessModel>();
	private LinkedList<MutantObject> mutants = new LinkedList<MutantObject>();
	
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


	public LinkedList<ProcessModel> getProcessModels() {
		return processModels;
	}


	public void addProcessModel(ProcessModel processModel) {
		this.processModels.add(processModel);
	}


	public LinkedList<MutantObject> getMutants() {
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
