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
@Table(name = "AUX_FILE", schema = "xpacs")
public class AuxFile implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String URI;
	
	@Column(nullable = false)
	private String Filename;
	
	private String Descriptor;
	
	@ManyToOne
	@JoinColumn(name = "PATIENT_HISTORY_Id", foreignKey = @ForeignKey(name = "FK_AUX_FILE_PATIENT_HISTORY_Id"))
	private PatientHistory patientHistory;
	
	protected AuxFile() {}
	
	public String toString() {
		return this.URI + "/" + this.Filename + ": " + this.Descriptor;
	}
	
}
