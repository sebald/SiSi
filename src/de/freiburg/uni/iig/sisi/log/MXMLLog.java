package de.freiburg.uni.iig.sisi.log;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MXMLLog {
	
	private static Document doc;
	private static Element rootElement;
	private static String log = "";
	
	public static String createMXML(TreeMap<String, EventLog> eventLogs) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    try {
	      DocumentBuilder builder = factory.newDocumentBuilder();
	      doc = builder.newDocument();
	    }catch (ParserConfigurationException parserException) {
	      parserException.printStackTrace();
	    }
		
		doc.appendChild(doc.createComment("MXML version 1.0"));
		doc.appendChild(doc.createComment("Created by SiSi (https://github.com/sebald/SiSi)"));
		doc.appendChild(doc.createComment("Author: Sebastian Sebald (sebastian.sebald@gmail.com)"));
		
		rootElement = doc.createElement("WorkflowLog");
		Attr attr = doc.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(attr);
		attr = doc.createAttribute("xsi:noNamespaceSchemaLocation");
		attr.setValue("http://is.tm.tue.nl/research/processmining/WorkflowLog.xsd");
		rootElement.setAttributeNode(attr);
		doc.appendChild(rootElement);
		
		Element sourceElement = doc.createElement("Source");
		attr = doc.createAttribute("program");
		attr.setValue("SiSi");
		sourceElement.setAttributeNode(attr);
		rootElement.appendChild(sourceElement);
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy@h:mm:ss");
		String formattedDate = sdf.format(date);

		
		Element processElement = doc.createElement("Process");
		attr = doc.createAttribute("id");
		attr.setValue("SiSiLog_" + formattedDate + ".mxml");
		processElement.setAttributeNode(attr);
		attr = doc.createAttribute("description");
		attr.setValue("Created with SiSi");
		processElement.setAttributeNode(attr);		
		rootElement.appendChild(processElement);
		
		// generate instances		
		for (Entry<String, EventLog> eventLogEntry  : eventLogs.entrySet()) {
			Element instanceElement = doc.createElement("ProcessInstance");
			attr = doc.createAttribute("id");
			attr.setValue(eventLogEntry.getKey());
			instanceElement.setAttributeNode(attr);
			processElement.appendChild(instanceElement);

			// add events
			for (SimulationEvent event : eventLogEntry.getValue().getEvents()) {
				Element auditTrailEntryElement = doc.createElement("AuditTrailEntry");
				instanceElement.appendChild(auditTrailEntryElement);
				
				// objects
				Element objectDataElement = doc.createElement("Data");
				auditTrailEntryElement.appendChild(objectDataElement);
				if( !event.getUsedObjects().isEmpty() ) {
					Element attributeElement = doc.createElement("Attribute");
					attr = doc.createAttribute("name");
					attr.setValue("Obejcts");
					attributeElement.setAttributeNode(attr);
					attributeElement.setTextContent(event.getUsedObjects().toString());
					objectDataElement.appendChild(attributeElement);
				}
				
				// task
				Element taskElement = doc.createElement("WorkflowModelElement");
				taskElement.setTextContent(event.getTransition().getName());
				auditTrailEntryElement.appendChild(taskElement);
				
				// event type
				Element eventTypeElement = doc.createElement("EventType");
				eventTypeElement.setTextContent("complete");
				auditTrailEntryElement.appendChild(eventTypeElement);
				
				// subject
				Element subjectElement = doc.createElement("Originator");
				subjectElement.setTextContent(event.getSubject().getName());
				auditTrailEntryElement.appendChild(subjectElement);
			}			
		}
		
		// write the XML document to disk

		try {
    		StringWriter sw = new StringWriter();
	        TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	  	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

	        transformer.transform(new DOMSource(doc), new StreamResult(sw));
	        log = sw.toString();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	    
	    return log.replace("-->", "-->"+System.getProperty("line.separator"));
	}
	

}
