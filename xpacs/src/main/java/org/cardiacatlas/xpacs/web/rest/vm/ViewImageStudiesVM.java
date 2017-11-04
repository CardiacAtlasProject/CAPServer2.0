package org.cardiacatlas.xpacs.web.rest.vm;

import org.cardiacatlas.xpacs.domain.dcm4che.DicomStudy;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Setter;

/**
* View Model object to view image studies
*
* @author Avan Suinesiaputra - 2017
* 
**/
public class ViewImageStudiesVM {
	
	@Setter private String patientID;
	@Setter private String patientName;
	@Setter private String birthDate;
	
	@JsonCreator
	public ViewImageStudiesVM() {
		// Empty public constructor used by Jackson
	}
	
	public ViewImageStudiesVM(DicomStudy ds) {
		this.patientID = ds.getPatientId().toString();
		this.patientName = ds.getPatientName().toString();
		this.birthDate = ds.getBirthDate().toString();
	}
		
	
	public String getPatientID() { return this.patientID; }
	public String getPatientName() { return this.patientName; }
	public String getBirthDate() { return this.birthDate; }
	
	@Override
	public String toString() {
		return "ViewImageStudiesVM{" +
				"patientID='" + patientID + '\'' +
				"patientName='" + patientName + '\'' +
				"birthDate='" + birthDate + '\'' + 
				'}';
	}
	

}
