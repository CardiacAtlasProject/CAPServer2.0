package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.ForeignKey;

import lombok.Data;

@Data
@Entity
@Table(name = "BASELINE_DIAGNOSIS", schema = "xpacs")
public class BaselineDiagnosis implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	private Float Age;
	private String Height;
	private String Weight;
	private String SBP;
	private String DBP;
	private String HeartRate;
	private String HistoryOfHypertension;
	private String HistoryOfDiabetes;
	private String HistoryOfAlcohol;
	private String HistoryOfSmoking;
	
	@ManyToOne
	@JoinColumn(name = "PATIENT_HISTORY_Id", foreignKey = @ForeignKey(name = "FK_BASELINE_DIAGNOSIS_PATIENT_HISTORY_Id"))
	private PatientHistory patientHistory;
	
	protected BaselineDiagnosis() {}
}
