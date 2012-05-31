package de.freiburg.uni.iig.sisi.model.policies;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class Policy extends ModelObject {
	
	public enum PolicyType {
		SEPERATION_OF_DUTY, 
		BINDING_OF_DUTY, 
		CONFLICT_OF_INTEREST, 
		USAGE_RESTRICTION, 
		ACTION_REQUIREMENT,
		UNKNOWN
	}
	private final PolicyType type;
	
	private Transition objective;
	private Transition eventually;
	
	public Policy(String id, String name, String type) {
		super(id, name);
		
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
		case "ua":
			this.type = PolicyType.USAGE_RESTRICTION;
			break;
		case "ar":
			this.type = PolicyType.ACTION_REQUIREMENT;
			break;			
		default:
			this.type = PolicyType.UNKNOWN;
			break;
		}
	}

	public Transition getObjective() {
		return objective;
	}

	public void setObjective(Transition objective) {
		this.objective = objective;
	}

	public Transition getEventually() {
		return eventually;
	}

	public void setEventually(Transition eventually) {
		this.eventually = eventually;
	}

	public PolicyType getType() {
		return type;
	}
	
}
