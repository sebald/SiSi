package de.freiburg.uni.iig.sisi.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import de.freiburg.uni.iig.sisi.model.ProcessModel;
import de.freiburg.uni.iig.sisi.model.net.variant.NetDeviation.DeviationType;
import de.freiburg.uni.iig.sisi.model.net.variant.VariantProcessModel;
import de.freiburg.uni.iig.sisi.utils.PNMLReader;

public class VariationTest {

	@Test
	public void testVariantProcessModelProcessModelDeviationType() throws ParserConfigurationException, SAXException, IOException {
		PNMLReader reader = new PNMLReader();
		ProcessModel pm = reader.createModelFromPNML("examples/kbv.pnml");		
		VariantProcessModel v = new VariantProcessModel(pm, DeviationType.AND2XOR);

		assertEquals("Is variant", true, pm.getNet().getPlaces().size() == (v.getNet().getPlaces().size()+2) );
		
		VariantProcessModel v1 = new VariantProcessModel(pm, DeviationType.XOR2AND);
		
		assertEquals("Is variant", true, pm.getNet().getPlaces().size() == (v1.getNet().getPlaces().size()-2) );
	}

}
