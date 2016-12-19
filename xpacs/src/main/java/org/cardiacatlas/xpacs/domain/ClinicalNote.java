package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
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

	@Column(nullable = false)
	private Date assessment_date;
	
	private Float age;
	private String weight;
	private String height;
	private String diagnosis;
	
	@Column(nullable = false)
	private String notes;
	
	@ManyToOne
	@JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "FK_CLINICAL_NOTE_PATIENT_INFO_Id"))
	private PatientInfo patientInfo;
	
	protected ClinicalNote() {}
		
}
