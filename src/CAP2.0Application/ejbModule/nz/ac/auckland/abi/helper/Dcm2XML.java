package nz.ac.auckland.abi.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.SAXWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Dcm2XML {

	ByteArrayOutputStream dicom;

	public Dcm2XML(File dicomFile) throws Exception {
		DicomInputStream dis = new DicomInputStream(dicomFile);
		DicomObject dco = dis.readDicomObject();
		dis.close();
		SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
		TransformerHandler th = tf.newTransformerHandler();
		th.getTransformer().setOutputProperty(OutputKeys.INDENT, "no");

		SAXWriter saxWriter = new SAXWriter(th, th);
		// Ignore bulk data
		int excludeTags[] = new int[1];
		excludeTags[0] = 0x7FE00010;
		saxWriter.setExclude(excludeTags);
		try {
			dicom = new ByteArrayOutputStream();
			th.setResult(new StreamResult(dicom));
			saxWriter.write(dco);
		} finally {
			if (dicom != null) {
				try {
					dicom.close();
				} catch (Exception ioe) {
					// ignore
				}
			}
		}
	}

	public String getXML() {
		return dicom.toString();
	}

	@SuppressWarnings("unchecked")
	public JSONObject getJSON() throws Exception {
		JSONObject result = new JSONObject();
		XMLStreamReader xr = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(dicom.toByteArray()));
		JSONArray tagArray = new JSONArray();

		String comment = null;
		while (xr.hasNext()) {
			int nextEvent = xr.next();
			if (nextEvent == XMLStreamConstants.COMMENT) {
				comment = xr.getText();
			} else if (nextEvent == XMLStreamConstants.START_ELEMENT) {
				JSONObject tag = new JSONObject();
				if (comment != null) {
					tag.put("name", comment);
					if (xr.getLocalName().equalsIgnoreCase("attr")) {
						int ac = xr.getAttributeCount();
						for (int i = 0; i < ac; i++) {
							tag.put(xr.getAttributeLocalName(i), xr.getAttributeValue(i));
						}
					}
					tagArray.add(tag);
				}
			}
		}
		result.put("DICOM", tagArray);
		return result;
	}
}
