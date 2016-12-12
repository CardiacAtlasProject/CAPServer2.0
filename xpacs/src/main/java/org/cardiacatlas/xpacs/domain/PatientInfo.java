package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema="xpacs", name="PATIENT_INFO")
public class PatientInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String PACS_Patient_Id;
	
	@Column(nullable = false)
	private String Gender;
	
	private String Ethnicity = null;
	
	@Column(nullable = false)
	private String Cohort;
	
	private String PrimaryDiagnosis = null;
	
	// no inheritance
	protected PatientInfo() {}
	
	public PatientInfo(String PACS_patient_id, String Gender) {
		this.PACS_Patient_Id = PACS_patient_id;
		this.Gender = Gender;
	}
	
	@Override
	public String toString() {
		return this.PACS_Patient_Id + "," + this.Gender + "," + this.Cohort;
	}
	
}
