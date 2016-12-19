package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema="xpacs", name="PATIENT_INFO")
public class PatientInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private String patient_id;
	
	@Column(nullable = false)
	private String gender;
	
	private String ethnicity = null;
	
	@Column(nullable = false)
	private String cohort;
	
	private String primary_diagnosis = null;
	
	// no inheritance
	protected PatientInfo() {}
	
	@Override
	public String toString() {
		return this.patient_id + "," + this.gender + "," + this.cohort;
	}
	
}
