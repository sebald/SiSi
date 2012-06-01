package de.freiburg.uni.iig.sisi.simulation;

import java.util.HashMap;
import java.util.HashSet;

import de.freiburg.uni.iig.sisi.model.net.Transition;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.Policy;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.UsageControl;


public class SimulationExcpetion extends Exception {

	private static final long serialVersionUID = 4790581174691832591L;

	public SimulationExcpetion(HashSet<Transition> needToBeFired) {
		super("Can not fire all transitions that are involved in a action requirement. The transition names are "+needToBeFired +".");
	}

	public SimulationExcpetion(HashMap<Transition, HashSet<UsageControl>> usageControlsToSatisfy) {
		super("Can not fire a transition, because of these usage control rules: " + usageControlsToSatisfy.values());
	}

	public SimulationExcpetion(Transition transition) {
		super("The transition "+ transition.getId() +" is part of an action requirement and an usage restrition rule.");
	}

	public SimulationExcpetion(Policy policy) {
		super("After satisfing the policy #" + policy.getId() + "there is now subject left that can execute the task.");
	}

	public SimulationExcpetion(UsageControl usageControl) {
		super("After satisfing the usage control #" + usageControl.getId() + "there is now subject left that can execute the task.");
	}

	
	
}
