package org.cardiacatlas.xpacs.dicom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DicomStudy {
	
	// DICOM Tag (0010,0020) = PatientID
	@JsonProperty("00100020")
	private DicomAttrSimpleString patientId;
	
	// DICOM Tag (0010,0010) = PatientName
	@JsonProperty("00100010")
	private DicomAttrPatientName patientName;
	
	// DICOM Tag (0010,0030) = BirthDate
	@JsonProperty("00100030")
	private DicomAttrSimpleString birthDate;
		
	public DicomStudy() {
		
	}
	
	public DicomAttrSimpleString getPatientId() { return this.patientId; }
	public void setPatientId( DicomAttrSimpleString _patientId ) { this.patientId = _patientId; }
	
	public DicomAttrPatientName getPatientName() { return this.patientName; }
	public void setPatientName( DicomAttrPatientName _patientName ) { this.patientName = _patientName; }
	
	public DicomAttrSimpleString getBirthDate() { return this.birthDate; }
	public void setBirthDate( DicomAttrSimpleString _birthDate ) { this.birthDate = _birthDate; }
	
	@Override
	public String toString() {
		return "{ PatientID: '" + patientId + ", " +
				"PatientName: '" + patientName + ", " +
				"BirthDate: '" + birthDate + ", " +
				'}';
	}

}
