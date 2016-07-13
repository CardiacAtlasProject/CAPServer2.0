package nz.auckland.abi.webservices;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAPData")
public class CAPData implements Serializable {

	private static final long serialVersionUID = -2910377403037621822L;
	@XmlElement(name = "dataType")
	String dataType;
	@XmlElement(name = "data")
	byte[] data;
}
