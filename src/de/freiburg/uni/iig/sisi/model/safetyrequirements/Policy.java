package de.freiburg.uni.iig.sisi.model.safetyrequirements;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class Policy extends ModelObject {
	
	public enum PolicyType {
		SEPERATION_OF_DUTY, 
		BINDING_OF_DUTY, 
		CONFLICT_OF_INTEREST,
		UNKNOWN
	}
	private final PolicyType type;
	
	private final Transition objective;
	private final Transition eventually;
	
	public Policy (String id, String name, PolicyType type, Transition objective, Transition eventually) {
		super(id, name);
		this.type = type;
		this.objective = objective;
		this.eventually = eventually;
	}
	
	public Policy(String id, String name, String type, Transition objective, Transition eventually) {
		super(id, name);
		
		this.objective = objective;
		this.eventually = eventually;
		
		switch (type) {
		case "sod":
			this.type = PolicyType.SEPERATION_OF_DUTY;
			break;
		case "bod":
			this.type = PolicyType.BINDING_OF_DUTY;
			break;
		case "coi":
			this.type = PolicyType.CONFLICT_OF_INTEREST;
			break;		
		default:
			this.type = PolicyType.UNKNOWN;
			break;
		}
	}

	public Transition getObjective() {
		return objective;
	}

	public Transition getEventually() {
		return eventually;
	}

	public PolicyType getType() {
		return type;
	}
	
}
