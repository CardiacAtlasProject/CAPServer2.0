package nz.auckland.abi.webservices;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAPSeries")
public class CAPSeries implements Serializable {

	private static final long serialVersionUID = -4129176426153718951L;
	@XmlElement(name = "seriesID")
	String seriesID;
	@XmlElement(name = "studyID")
	String studyID;
	@XmlElement(name = "modality")
	String modality;

	@Override
	public String toString() {
		return "CAPSeries [seriesID=" + seriesID + ", studyID=" + studyID + ", modality=" + modality + "]";
	}

}
