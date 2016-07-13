package nz.auckland.abi.webservices;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAPSubject")
public class CAPSubject implements Serializable {

	private static final long serialVersionUID = 1541930537420808995L;
	@XmlElement(name = "id")
	String id;
	@XmlElement(name = "name")
	String name;
	@XmlElement(name = "birthdate")
	String birthdate;
	@XmlElement(name = "gender")
	String gender;

	@Override
	public String toString() {
		return "CAPSubject [id=" + id + ", name=" + name + ", birthdate=" + birthdate + ", gender=" + gender + "]";
	}

}
