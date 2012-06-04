package sisi;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.log.LogGenerator;
import de.freiburg.uni.iig.sisi.log.LogGenerator.FileMode;
import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration;
import de.freiburg.uni.iig.sisi.simulation.SimulationConfiguration.ResourceSelectionMode;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;
import de.freiburg.uni.iig.sisi.simulation.SimulationExcpetion;

public class Testing {

	public static void main(String[] args) {

		try {
			ProcessModel pm = new ProcessModel("examples/kbv.pnml");
			
			// this will be later generated through the UI
			SimulationConfiguration conf = new SimulationConfiguration(ResourceSelectionMode.RANDOM, true);	
			conf.addProcessModel(pm);
			
//			VariantProcessModel v = new VariantProcessModel("examples/kbv.pnml", DeviationType.SKIPPING);
//			conf.addProcessModel(v);
//			
//			MutantObject mutant1 = MutantFactory.createMutantFrom(pm.getNet().getTransitions().get(1), pm);
//			MutantObject mutant2 = MutantFactory.createMutantFrom(pm.getSafetyRequirements().getPolicies().get(0), pm);
//			MutantObject mutant3 = MutantFactory.createMutantFrom(pm.getSafetyRequirements().getUsageControls().get(0), pm);
//			conf.addMutant(mutant1);
//			conf.addMutant(mutant2);
//			conf.addMutant(mutant3);
			
			SimulationEngine se = new SimulationEngine(conf);
			LogGenerator lg = new LogGenerator(se, FileMode.CSV);
			se.runFor(2);
			String log = lg.generateLog(false);
			System.out.println(log);
			
			
			
//			System.out.println(v.getNet().getTransitions());
			
		} catch (ParserConfigurationException | SAXException | IOException | SimulationExcpetion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}