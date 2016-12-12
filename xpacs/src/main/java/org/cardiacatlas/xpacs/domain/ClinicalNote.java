package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "CLINICAL_NOTE", schema = "xpacs")
public class ClinicalNote implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Float Age;
	private String Weight;
	private String Height;
	private String Diagnosis;
	private String Notes;
	
	@ManyToOne
	@JoinColumn(name = "PATIENT_HISTORY_Id", foreignKey = @ForeignKey(name = "FK_CLINICAL_NOTE_PATIENT_HISTORY_Id"))
	private PatientHistory patientHistory;
	
	protected ClinicalNote() {}
		
}
