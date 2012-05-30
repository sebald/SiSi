package sisi;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.log.LogGenerator;
import de.freiburg.uni.iig.sisi.simulation.SimulationEngine;
import de.freiburg.uni.iig.sisi.simulation.SimulationModel;

public class Testing {

	public static void main(String[] args) {

		try {
			SimulationModel sm = new SimulationModel("examples/kbv.pnml");
//			System.out.println(sm.getNet().getName());
			
			SimulationEngine se = new SimulationEngine(sm);
			LogGenerator lg = new LogGenerator(se);
			se.run();
			lg.generateLog();
			
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
//		Display display = new Display();
//
//		Shell shell = new Shell(display);
//		shell.open();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch())
//				display.sleep();
//		}
//		display.dispose();
	}
}