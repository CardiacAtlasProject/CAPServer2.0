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
@Table(name = "PATIENT_HISTORY", schema = "xpacs")
public class PatientHistory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(nullable = false)
	private Date event_date;
	
	private Date created_time;
	
	private Date modified_time;

	@ManyToOne
	@JoinColumn(name = "pacs_patient_id", foreignKey = @ForeignKey(name = "FK_PATIENT_HISTORY_PATIENT_INFO_Id"))
	private PatientInfo patientInfo;
	
	protected PatientHistory() {}
	
}
