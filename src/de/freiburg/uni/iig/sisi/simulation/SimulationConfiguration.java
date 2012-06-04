package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import de.freiburg.uni.iig.sisi.log.LogGenerator.FileMode;
import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.variant.NetDeviation.DeviationType;

public class SimulationConfiguration {

	public enum ResourceSelectionMode {
		LIST, RANDOM
	}
	
	// configuration parameters
	private ResourceSelectionMode resourceSelectionMode;
	private boolean considerSafetyRequirements;
	
	private int runsWithoutViolations = 0;
	private int runsViolatingAuthorizations = 0;
	private HashMap<ModelObject, Integer> violationMap = new HashMap<ModelObject, Integer>();
	
	private HashMap<DeviationType, Integer> deviationMap = new HashMap<DeviationType, Integer>();
	
	private FileMode fileMode = FileMode.CSV;
	private String saveLogPath = "";
	private boolean seperateLogs = false;

	private LinkedList<ProcessModel> processModels = new LinkedList<ProcessModel>();
	private LinkedList<MutantObject> mutants = new LinkedList<MutantObject>();
	
	private HashMap<ModelObject, HashSet<MutantObject>> activatorMap = new HashMap<ModelObject, HashSet<MutantObject>>();
	
	public SimulationConfiguration(ResourceSelectionMode resourceSelectionMode, boolean considerSafetyRequirements) {
		this.resourceSelectionMode = resourceSelectionMode;
		this.considerSafetyRequirements = considerSafetyRequirements;
	}
	
	public void setResourceSelectionMode(ResourceSelectionMode resourceSelectionMode) {
		this.resourceSelectionMode = resourceSelectionMode;
	}
	
	public ResourceSelectionMode getResourceSelectionMode() {
		return resourceSelectionMode;
	}

	public void setConsiderSafetyRequirements(boolean considerSafetyRequirements) {
		this.considerSafetyRequirements = considerSafetyRequirements;
	}

	public boolean isConsiderSafetyRequirements() {
		return considerSafetyRequirements;
	}

	public int getRunsWithoutViolations() {
		return runsWithoutViolations;
	}

	public void setRunsWithoutViolations(int runsWithoutViolations) {
		this.runsWithoutViolations = runsWithoutViolations;
	}

	public int getRunsViolatingAuthorizations() {
		return runsViolatingAuthorizations;
	}

	public void setRunsViolatingAuthorizations(int runsViolatingAuthorizations) {
		this.runsViolatingAuthorizations = runsViolatingAuthorizations;
	}

	public HashMap<ModelObject, Integer> getViolationMap() {
		return violationMap;
	}

	public void removeFromViolationMap(ModelObject modelObject) {
		this.violationMap.remove(modelObject);
	}
	
	public void updateViolationMap(ModelObject modelObject, int i) {
		this.violationMap.put(modelObject, i);
	}
	
	public HashMap<DeviationType, Integer> getDeviationMap() {
		return deviationMap;
	}

	public void removeFromDeviationMap(DeviationType type) {
		this.deviationMap.remove(type);
	}
	
	public void updateDeivationMap(DeviationType type, int i) {
		this.deviationMap.put(type, i);
	}
	
	public FileMode getFileMode() {
		return fileMode;
	}

	public void setFileMode(FileMode fileMode) {
		this.fileMode = fileMode;
	}

	public String getSaveLogPath() {
		return saveLogPath;
	}

	public void setSaveLogPath(String saveLogPath) {
		this.saveLogPath = saveLogPath;
	}

	public boolean isSeperateLogs() {
		return seperateLogs;
	}

	public void setSeperateLogs(boolean seperateLogs) {
		this.seperateLogs = seperateLogs;
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
