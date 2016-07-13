package nz.auckland.abi.webservices;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAPModel")
public class CAPModel implements Serializable {

	private static final long serialVersionUID = -7198439625519000213L;
	@XmlElement(name = "modelID")
	Long modelID;
	@XmlElement(name = "studyID")
	String studyID;
	@XmlElement(name = "modelName")
	String modelName;
	@XmlElement(name = "modelComments")
	String modelComments;

	@Override
	public String toString() {
		return "CAPModel [modelID=" + modelID + ", studyID=" + studyID + ", modelName=" + modelName + ", modelComments=" + modelComments + "]";
	}

}
