package org.cardiacatlas.xpacs.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Patient {
	
	@Id
	private int id;
	
	private String PatientID;
	private String Age;
	private String Gender;
	private String Ethnicity;
	private String Cohort;
	
	// setters
	public void setId(int _id) { this.id = _id; }
	public void setPatientID(String _patientID) { this.PatientID = _patientID; }
	public void setAge(String _age) { this.Age = _age; }
	public void setGender(String _gender) { this.Gender = _gender; }
	public void setEthnicity(String _ethnicity) { this.Ethnicity = _ethnicity; }
	public void setCohort(String _cohort) { this.Cohort = _cohort; }
	
	// getters
	public int getId() { return this.id; }
	public String getPatientID() { return this.PatientID; }
	public String getAge() { return this.Age; }
	public String getGender() { return this.Gender; }
	public String getEthnicity() { return this.Ethnicity; }
	public String getCohort() { return this.Cohort; }
	
	// String representation
	@Override
	public String toString() {
		return String.format("{%d: %s, %s, %s, %s, %s}", this.id,
				this.PatientID, this.Age, this.Gender, this.Ethnicity, this.Cohort);
	}

}
