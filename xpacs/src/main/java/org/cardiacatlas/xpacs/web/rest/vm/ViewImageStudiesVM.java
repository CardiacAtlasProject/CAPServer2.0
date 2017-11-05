package org.cardiacatlas.xpacs.web.rest.vm;

public class ViewImageStudiesVM {
	
	private String patientId;
	private String studyIuid = "";
	private String studyDate = "";
	private String studyDesc = "";
	
	public ViewImageStudiesVM patientId(String _patientId) { this.patientId = _patientId; return this; }
	public ViewImageStudiesVM studyIuid(String _studyIuid) { this.studyIuid = _studyIuid; return this; }
	public ViewImageStudiesVM studyDate(String _studyDate) { this.studyDate = _studyDate; return this; }
	public ViewImageStudiesVM studyDesc(String _studyDesc) { this.studyDesc = _studyDesc; return this; }
	
	public String getPatientId() { return this.patientId; }
	public String getStudyIuid() { return this.studyIuid; }
	public String getStudyDate() { return this.studyDate; }
	public String getStudyDesc() { return this.studyDesc; }
	
	@Override
	public String toString() {
		return "[PatientId = '" + this.patientId + "', " +
				"StudyIUID = '" + this.studyIuid + "', " + 
				"StudyDate = '" + this.studyDate + "', " +
				"StudyDesc = '" + this.studyDesc + "']";
	}

}
