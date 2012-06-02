package sisi;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.log.LogGenerator;
import de.freiburg.uni.iig.sisi.model.MutantObject;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.safetyrequirements.mutant.MutantFactory;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration.ResourceSelectionMode;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine.ModelState;
import de.freiburg.uni.iig.sisi.simulation.SimulationExcpetion;

public class Testing {

	public static void main(String[] args) {

		try {
			ProcessModel pm = new ProcessModel("examples/kbv.pnml");
			
			// this will be later generated through the UI
			SimulationConfiguration conf = new SimulationConfiguration(ResourceSelectionMode.RANDOM, true);			
//			MutantObject mutant = MutantFactory.createMutantFrom(pm.getNet().getTransitions().get(1), pm);
			MutantObject mutant2 = MutantFactory.createMutantFrom(pm.getSafetyRequirements().getPolicies().get(0), pm);
			
			conf.addMutant(mutant2);
			
			SimulationEngine se = new SimulationEngine(pm, conf);
			LogGenerator lg = new LogGenerator(se);
			ModelState modelState = se.run();
			String log = lg.generateLog();
			
			System.out.println(log);
			System.out.println(modelState);
			
		} catch (ParserConfigurationException | SAXException | IOException | SimulationExcpetion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}