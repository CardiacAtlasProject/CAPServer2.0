package org.cardiacatlas.xpacs.domain;

import java.util.List;

import org.cardiacatlas.xpacs.repository.AuxFileRepository;

public class ConsolidatedInfo {
	private PatientInfo patientInfo;
	private List<ClinicalNote> clinicalNote;
	private List<BaselineDiagnosis> baselineDiagnosis;
	private List<AuxFile> auxFile;
	private List<CapModel> capModel;
	public ConsolidatedInfo(Long id,PatientInfo patientInfo, List<ClinicalNote> clinicalNote,List<BaselineDiagnosis> baselineDiagnosis,List<AuxFile> auxFile,List<CapModel> capModel) {
		
		this.patientInfo = patientInfo;
		this.clinicalNote = clinicalNote;
		this.baselineDiagnosis = baselineDiagnosis;
		this.auxFile = auxFile;
		this.capModel = capModel;
	}
	public List<AuxFile> getAuxFile() {
		return auxFile;
	}
	public void setAuxFile(List<AuxFile> auxFile) {
		this.auxFile = auxFile;
	}
	public List<CapModel> getCapModel() {
		return capModel;
	}
	public void setCapModel(List<CapModel> capModel) {
		this.capModel = capModel;
	}
	public List<BaselineDiagnosis> getBaselineDiagnosis() {
		return baselineDiagnosis;
	}
	public void setBaselineDiagnosis(List<BaselineDiagnosis> baselineDiagnosis) {
		this.baselineDiagnosis = baselineDiagnosis;
	}
	public PatientInfo getPatientInfo() {
		return patientInfo;
	}
	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}
	public List<ClinicalNote> getClinicalNote() {
		return clinicalNote;
	}
	public void setClinicalNote(List<ClinicalNote> clinicalNote) {
		this.clinicalNote = clinicalNote;
	}
	
}
