package org.cardiacatlas.xpacs.web.rest.vm;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.domain.enumeration.GenderType;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.Setter;

/**
 * View Model object to view patient info status
 *
 * @author Avan Suinesiaputra - 2017
 * 
 **/
public class ViewPatientInfoVM {
	
	@Setter private String patientID;
	@Setter private GenderType gender;
	@Setter private String cohort;
	@Setter private String ethnicity;
	@Setter private String primaryDiagnosis;
	
	@JsonCreator
	public ViewPatientInfoVM() {
		// Empty public constructor used by Jackson
	}
	
	public ViewPatientInfoVM(PatientInfo _pat) {
		this.patientID = _pat.getPatientId();
		this.gender = _pat.getGender();
		this.cohort = _pat.getCohort();
		this.ethnicity = _pat.getEthnicity();
		this.primaryDiagnosis = _pat.getPrimaryDiagnosis();
	}
	
	public String getGender() {
		return this.gender.toString();
	}
	
	public String getPatientID() { return this.patientID; }
	public String getCohort() { return this.cohort; }
	public String getEthnicity() { return this.ethnicity; }
	public String getPrimaryDiagnosis() { return this.primaryDiagnosis; }
	
	@Override
	public String toString() {
		return "ViewPatientInfoVM{" +
				"patientID='" + patientID + '\'' +
				"gender='" + gender.toString() + '\'' +
				"cohort='" + cohort + '\'' + 
				"ethnicity='" + ethnicity + '\'' + 
				"primaryDiagnosis='" + primaryDiagnosis + '\'' + 
				'}';
	}

}
