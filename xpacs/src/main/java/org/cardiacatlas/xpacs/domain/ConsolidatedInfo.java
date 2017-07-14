package org.cardiacatlas.xpacs.domain;

import org.cardiacatlas.xpacs.repository.AuxFileRepository;

public class ConsolidatedInfo {
	private PatientInfo patientInfo;
	private ClinicalNote clinicalNote;
	private BaselineDiagnosis baselineDiagnosis;
	private final AuxFile auxFile;
	public ConsolidatedInfo(Long id,PatientInfo patientInfo, ClinicalNote clinicalNote,BaselineDiagnosis baselineDiagnosis,AuxFile auxFile) {
		
		this.patientInfo = patientInfo;
		this.clinicalNote = clinicalNote;
		this.baselineDiagnosis = baselineDiagnosis;
		this.auxFile = auxFile;
	}
	public BaselineDiagnosis getBaselineDiagnosis() {
		return baselineDiagnosis;
	}
	public void setBaselineDiagnosis(BaselineDiagnosis baselineDiagnosis) {
		this.baselineDiagnosis = baselineDiagnosis;
	}
	public PatientInfo getPatientInfo() {
		return patientInfo;
	}
	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}
	public AuxFile getAuxFile() {
		return auxFile;
	}
	public ClinicalNote getClinicalNote() {
		return clinicalNote;
	}
	public void setClinicalNote(ClinicalNote clinicalNote) {
		this.clinicalNote = clinicalNote;
	}
	@Override
	public String toString(){
		return "PatientInfo{" +
	            "id=" + patientInfo.getId() +
	            ", patient_id='" + patientInfo.getPatient_id() + "'" +
	            ", cohort='" + patientInfo.getCohort() + "'" +
	            ", ethnicity='" + patientInfo.getEthnicity() + "'" +
	            ", gender='" + patientInfo.getGender() + "'" +
	            ", primary_diagnosis='" + patientInfo.getPrimary_diagnosis() + "'" +
	            '}'
	            ;
	}
	
}
