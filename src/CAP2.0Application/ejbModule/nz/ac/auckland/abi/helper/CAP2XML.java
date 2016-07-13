package nz.ac.auckland.abi.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * Helper class to convert CAP model description xml to Java object Currently
 * determines the studyuid, modelname, series information and provenence data
 * 
 * @author jagir
 *
 */

public class CAP2XML {
	private String studyUID;
	private String modelName;
	private HashSet<String> series;
	private HashMap<String, String> documentation;
	private String date;
	private byte[] xml;

	public CAP2XML(File file) throws Exception {
		xml = Files.readAllBytes(file.toPath());
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xml));

		String tagContent = null;
		series = new HashSet<String>();
		documentation = new HashMap<String, String>();
		while (reader.hasNext()) {
			int event = reader.next();

			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if ("Analysis".equals(reader.getLocalName())) {
					int attr = reader.getAttributeCount();
					for (int i = 0; i < attr; i++) {
						if (reader.getAttributeLocalName(i).equalsIgnoreCase("name")) {
							modelName = reader.getAttributeValue(i);
						} else if (reader.getAttributeLocalName(i).equalsIgnoreCase("studyiuid")) {
							studyUID = reader.getAttributeValue(i);
						}
					}
				} else if ("Image".equals(reader.getLocalName())) {
					int attr = reader.getAttributeCount();
					for (int i = 0; i < attr; i++) {
						if (reader.getAttributeLocalName(i).equalsIgnoreCase("seriesiuid")) {
							series.add(reader.getAttributeValue(i));
							break;
						}
					}
				} else if ("provenanceDetail".equals(reader.getLocalName())) {
					int attr = reader.getAttributeCount();
					for (int i = 0; i < attr; i++) {
						if (reader.getAttributeLocalName(i).equalsIgnoreCase("date")) {
							date = reader.getAttributeValue(i);
							break;
						}
					}
				}
				break;

			case XMLStreamConstants.CHARACTERS:
				tagContent = reader.getText().trim();
				break;

			case XMLStreamConstants.END_ELEMENT:
				switch (reader.getLocalName()) {
				case "operatingSystem":
					documentation.put("operatingSystem", tagContent);
					break;
				case "package":
					documentation.put("package", tagContent);
					break;
				case "platform":
					documentation.put("platform", tagContent);
					break;
				case "process":
					documentation.put("process", tagContent);
				case "program":
					documentation.put("program", tagContent);
				case "programParams":
					documentation.put("programParams", tagContent);
				case "programVersion":
					documentation.put("programVersion", tagContent);
				case "step":
					documentation.put("step", tagContent);
				case "comment":
					documentation.put("comment", tagContent);
					break;
				}
				break;

			}

		}

	}

	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append("Model name : "+modelName+"\n");
		buf.append("StudyIUID : "+studyUID+"\n");
		buf.append("Date : "+date+"\n");
		buf.append("Used Series  : ("+series.size()+")\n");

		for(String s : series){
			buf.append("\t"+s +"\n");
		}
		for(String k : documentation.keySet()){
			buf.append(k+"\t: "+documentation.get(k)+"\n");
		}
		return buf.toString();
	}
	
	public String getStudyUID() {
		return studyUID;
	}

	public String getModelName() {
		return modelName;
	}

	public HashSet<String> getSeries() {
		return series;
	}

	public HashMap<String, String> getDocumentation() {
		return documentation;
	}

	public String getDate() {
		return date;
	}

	public byte[] getXmlBytes() {
		return xml;
	}
	
	public static void main(String args[]) throws Exception {
		CAP2XML xml = new CAP2XML(new File("/home/jagir/Downloads/SCD_CAPModels/SCD0000101/model_SCD0000101_ao/model_SCD0000101_ao.xml"));
		System.out.println(xml);
	}
}
