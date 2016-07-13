package nz.auckland.abi.webservices;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAPStudy")
public class CAPStudy implements Serializable {

	private static final long serialVersionUID = -7077910597195175398L;
	@XmlElement(name = "studyID")
	String studyID;
	@XmlElement(name = "subjectID")
	String subjectID;
	@XmlElement(name = "studyModalities")
	String studyModalities;
	@XmlElement(name = "studyDate")
	Date studyDate;
	@XmlElement(name = "studyDescription")
	String studyDescription;

	@Override
	public String toString() {
		return "CAPStudy [studyID=" + studyID + ", subjectID=" + subjectID + ", studyModalities=" + studyModalities + ", studyDate=" + studyDate
				+ ", studyDescription=" + studyDescription + "]";
	}

}
