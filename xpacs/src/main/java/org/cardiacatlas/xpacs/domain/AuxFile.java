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
@Table(name = "AUX_FILE", schema = "xpacs")
public class AuxFile implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String uri;
	
	@Column(nullable = false)
	private String filename;
	
	@Column(nullable = false)
	private Date created_date;
	
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "FK_AUX_FILE_PATIENT_INFO_Id"))
	private PatientInfo patientInfo;
	
	protected AuxFile() {}
	
	public String toString() {
		return this.uri + "/" + this.filename + ": " + this.description;
	}
	
}
