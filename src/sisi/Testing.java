package sisi;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.log.LogGenerator;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.MutantFactory;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine.ModelState;
import de.freiburg.uni.iig.sisi.simulation.SimulationExcpetion;

public class Testing {

	public static void main(String[] args) {

		try {
			ProcessModel pm = new ProcessModel("examples/kbv.pnml");
			
			SimulationEngine se = new SimulationEngine(pm);
			LogGenerator lg = new LogGenerator(se);
			ModelState modelState = se.run();
			String log = lg.generateLog();
			
			
			System.out.println(pm.getResourceModel().getSubjects());
			
			MutantFactory.createMutantFrom(pm.getNet().getTransitions().get(0), pm);
			
			System.out.println(log);
			System.out.println(modelState);
			
		} catch (ParserConfigurationException | SAXException | IOException | SimulationExcpetion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}