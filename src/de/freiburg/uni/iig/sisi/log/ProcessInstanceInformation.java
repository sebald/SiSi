package de.freiburg.uni.iig.sisi.log;

import java.util.List;

import de.freiburg.uni.iig.sisi.model.ModelObject;
import de.freiburg.uni.iig.sisi.model.net.Node;
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
		
		String s = "RunID: " + getId() + nl + "Deviation Type: " + deviationType + nl + "Details: ";
		
		switch (deviationType) {
		case NONE:
			s += "original model";
			break;
		case SKIPPING:
			s += "Skipping Task \"" + deviation.getOldValues().get(0) + "\"";
			break;
		case SWAPPING:
			s += "Position of Task \"" + deviation.getOldValues().get(0) + "\" has been swapped with Position of Task \"" + deviation.getOldValues().get(1) + "\"";
			break;
		case AND2XOR:
			List<Node> firstPlaces = deviation.getOldValues().subList(0, deviation.getOldValues().size()/2);
			List<Node> secondPlaces = deviation.getOldValues().subList((deviation.getOldValues().size()/2)+1, deviation.getOldValues().size());
			s += "Merged " + firstPlaces + " to one place with ID " + deviation.getNewValues().get(0);
			s += " and merged " + secondPlaces + " to one place with ID " + deviation.getNewValues().get(1);
			break;
		case XOR2AND:
			List<Node> firstPlace = deviation.getNewValues().subList(0, deviation.getNewValues().size()/2);
			List<Node> secondPlace = deviation.getNewValues().subList((deviation.getNewValues().size()/2)+1, deviation.getNewValues().size());
			s += "Splitted " + deviation.getOldValues().get(0) + " to places with IDs " + firstPlace;
			s += " and splitted " + deviation.getOldValues().get(1) + " to places with IDs " + secondPlace;
			break;
		default:
			break;
		}
		
		return s;
	}

	
	
}
