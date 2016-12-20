package org.cardiacatlas.xpacs.domain;

import java.io.Serializable;
import java.sql.Blob;
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
@Table(name = "CAP_MODEL", schema = "xpacs")
public class CAPModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Date created_date;
	
	private String type;
	private String comment;
	
	private Blob model_data;
	private Blob xml_data;
	
	@ManyToOne
	@JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "FK_CAP_MODEL_PATIENT_INFO_Id"))
	private PatientInfo patientInfo;
	
	protected CAPModel() {}
	
	public String getPatientId() { return this.patientInfo.getPatient_id(); }
	
}
