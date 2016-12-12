package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;
import java.sql.Blob;

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
@Table(name = "CAP_MODEL", schema = "xpacs")
public class CAPModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	private String Name;
	private String Type;
	private String Comment;
	
	private Blob ModelData;
	private Blob XMLData;
	
	@ManyToOne
	@JoinColumn(name = "PATIENT_HISTORY_Id", foreignKey = @ForeignKey(name = "FK_CAP_MODEL_PATIENT_HISTORY_Id"))
	private PatientHistory patientHistory;
	
	protected CAPModel() {}
	
}
