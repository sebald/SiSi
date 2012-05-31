package de.freiburg.uni.iig.sisi.model.safetyrequirements;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.net.Transition;

public class UsageControl extends ModelObject {

	public enum UsageControlType {
		USAGE_RESTRICTION, 
		ACTION_REQUIREMENT,
		UNKNOWN
	}
	private final UsageControlType type;
	
	private final Transition objective;
	private final Transition eventually;
	
	public UsageControl(String id, String name, String type, Transition objective, Transition eventually) {
		super(id, name);
		
		this.objective = objective;
		this.eventually = eventually;
		
		switch (type) {
		case "ur":
			this.type = UsageControlType.USAGE_RESTRICTION;
			break;
		case "ar":
			this.type = UsageControlType.ACTION_REQUIREMENT;
			break;			
		default:
			this.type = UsageControlType.UNKNOWN;
			break;
		}
		
	}
	
	public Transition getObjective() {
		return objective;
	}

	public Transition getEventually() {
		return eventually;
	}

	public UsageControlType getType() {
		return type;
	}	
	
}
