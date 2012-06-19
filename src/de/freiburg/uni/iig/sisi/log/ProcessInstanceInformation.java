package de.freiburg.uni.iig.sisi.log;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.net.variant.NetDeviation;
import de.freiburg.uni.iig.sisi.model.net.variant.NetDeviation.DeviationType;

public class ProcessInstanceInformation extends ModelObject {
	
	private NetDeviation deviation = null;
	private DeviationType deviationType = DeviationType.NONE;
	
	public ProcessInstanceInformation(String id, String name) {
		super(id, name);
	}	
	
	public NetDeviation getDeviation() {
		return deviation;
	}

	public void setDeviation(NetDeviation deviation) {
		this.deviation = deviation;
		this.deviationType = deviation.getType();
	}

	public DeviationType getDeviationType() {
		return deviationType;
	}

	@Override
	public String toString() {
		String nl = System.lineSeparator();
		
		String s = "RunID: " + getId() + nl + "Deviation Type: " + deviationType;
		if ( deviation != null ) {
			s += nl + "Original Values: " + deviation.getOldValues() + nl;
			s += "New Values: " + deviation.getNewValues();
			
		}
		
		return s;
	}

	
	
}
