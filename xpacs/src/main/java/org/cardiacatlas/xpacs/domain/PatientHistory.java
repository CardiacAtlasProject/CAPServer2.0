package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	private String PACS_Patient_Id;
	
	private String PACS_Study_Iuid;
	
	private Date EventDate;

	protected PatientHistory() {}
	
}
