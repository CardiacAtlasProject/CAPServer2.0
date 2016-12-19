package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;
import java.sql.Date;

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
	
	@Column(nullable = false)
	private Date diagnosis_date;
	
	private Float age;
	private String height;
	private String weight;
	private String sbp;
	private String dbp;
	private String heart_rate;
	private String history_of_hypertension;
	private String history_of_diabetes;
	private String history_of_alcohol;
	private String history_of_smoking;
	
	@ManyToOne
	@JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "FK_BASELINE_DIAGNOSIS_PATIENT_INFO_Id"))
	private PatientInfo patientInfo;
	
	protected BaselineDiagnosis() {}
}
