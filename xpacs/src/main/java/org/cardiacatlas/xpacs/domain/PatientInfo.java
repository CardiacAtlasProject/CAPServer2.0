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
	
	/**
	 * id (PRI, AUTO)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	
	/**
	 * PACS_patient_id (UNI). The ID from PACS database
	 */
	@Column(name = "PACS_patient_id", unique = true, length = 16, nullable = false)
	private String PACS_patient_id;
	
	/**
	 * Age of a patient.
	 */
	@Column(name = "Age", nullable = false)
	private Float Age;
	
	/**
	 * Gender
	 */
	@Column(name = "Gender", length = 45, nullable = false)
	private String Gender;
	
	/**
	 * Ethnicity
	 */
	@Column(name = "Ethnicity", length = 45)
	private String Ethnicity = null;
	
	/**
	 * Cohort
	 */
	@Column(name = "Cohort", length = 45, nullable = false)
	private String Cohort;
	
	/**
	 * PrimaryDiagnosis
	 */
	@Column(name = "PrimaryDiagnosis", length = 255)
	private String PrimaryDiagnosis = null;
	
	// no inheritance
	protected PatientInfo() {}
	
	public PatientInfo(String PACS_patient_id, Float Age, String Gender) {
		this.PACS_patient_id = PACS_patient_id;
		this.Age = Age;
		this.Gender = Gender;
	}
	
	@Override
	public String toString() {
		return this.getPACS_patient_id() + "," + this.getAge().toString() + "," + this.getGender() + this.getCohort();
	}
	
}
