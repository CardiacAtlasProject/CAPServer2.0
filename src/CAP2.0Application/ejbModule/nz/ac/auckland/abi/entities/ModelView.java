/*******************************************************************************
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License
 *  Version 1.1 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an "AS IS"
 *  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 *  License for the specific language governing rights and limitations
 *  under the License.
 *
 *  The Original Code is ICMA
 *
 *  The Initial Developer of the Original Code is University of Auckland,
 *  Auckland, New Zealand.
 *  Copyright (C) 2011-2014 by the University of Auckland.
 *  All Rights Reserved.
 *
 *  Contributor(s): Jagir R. Hussan
 *
 *  Alternatively, the contents of this file may be used under the terms of
 *  either the GNU General Public License Version 2 or later (the "GPL"), or
 *  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 *  in which case the provisions of the GPL or the LGPL are applicable instead
 *  of those above. If you wish to allow use of your version of this file only
 *  under the terms of either the GPL or the LGPL, and not to allow others to
 *  use your version of this file under the terms of the MPL, indicate your
 *  decision by deleting the provisions above and replace them with the notice
 *  and other provisions required by the GPL or the LGPL. If you do not delete
 *  the provisions above, a recipient may use your version of this file under
 *  the terms of any one of the MPL, the GPL or the LGPL.
 *
 *
 *******************************************************************************/
package nz.ac.auckland.abi.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "ModelView")
public class ModelView implements Serializable{
	private static final long serialVersionUID = 6898L;

    @Column(name = "subject_id")
	private String subjectID;
    
	@Column(name = "subject_name")
    private String subjectName;

    @Column(name = "subject_birthdate")
    private String birthdate;

    @Column(name = "subject_gender")
    private String gender;

	@Column(name = "study_id")
	private String studyID;
	
	@Column(name = "study_modalities")
	private String studyModalities;

	@Column(name = "study_date")
	private java.sql.Timestamp studyDate;

	@Column(name = "study_description")
	private String studyDescription;
	
	@Id
	@Column(name = "model_id")
	private Long modelID;
	
	@Column(name = "model_name")
	private String modelName;
	
	@Column(name = "model_comments")
	private String modelComments;
	
	@Column(name="model_hasMetaData")
	private Integer hasMetaData;
	
	public ModelView(){
		super();
	}
	
	public ModelView(String id, String name, String dob, String gender, String sid, String sModalities, String sDate,
			String sDes, Long modelid, String modelname, String comments) throws Exception{
		this.subjectID = id;
		this.subjectName = name;
		this.birthdate = dob;
		this.gender = gender;
		studyID = sid;
		studyDescription = sDes;
		studyModalities = sModalities;
		modelID = modelid;
		modelName = modelname;
		modelComments = comments;
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"dd-MM-yyyy kk:mm:ss.SSSS", Locale.ENGLISH);
			Date myDate = format.parse(sDate.trim());
			studyDate = new java.sql.Timestamp(myDate.getTime());
		} catch (Exception exx) {
			Logger log = Logger.getLogger(Study.class.getSimpleName());
			log.log(Level.SEVERE, "Unable to set the study date for study "
					+ sid + "\t" + sDate + "\t" + exx);
			throw exx;
		}
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getStudyID() {
		return studyID;
	}

	public void setStudyID(String studyID) {
		this.studyID = studyID;
	}

	public String getStudyModalities() {
		return studyModalities;
	}

	public void setStudyModalities(String studyModalities) {
		this.studyModalities = studyModalities;
	}

	public java.sql.Timestamp getStudyDate() {
		return studyDate;
	}

	public void setStudyDate(java.sql.Timestamp studyDate) {
		this.studyDate = studyDate;
	}

	public String getStudyDescription() {
		return studyDescription;
	}

	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}

	public Long getModelID() {
		return modelID;
	}

	public void setModelID(Long modelID) {
		this.modelID = modelID;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelComments() {
		return modelComments;
	}

	public void setModelComments(String modelComments) {
		this.modelComments = modelComments;
	}

	public Integer getHasMetaData() {
		return hasMetaData;
	}

	public void setHasMetaData(Integer hasMetaData) {
		this.hasMetaData = hasMetaData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modelID == null) ? 0 : modelID.hashCode());
		result = prime * result + ((studyID == null) ? 0 : studyID.hashCode());
		result = prime * result + ((subjectID == null) ? 0 : subjectID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelView other = (ModelView) obj;
		if (modelID == null) {
			if (other.modelID != null)
				return false;
		} else if (!modelID.equals(other.modelID))
			return false;
		if (studyID == null) {
			if (other.studyID != null)
				return false;
		} else if (!studyID.equals(other.studyID))
			return false;
		if (subjectID == null) {
			if (other.subjectID != null)
				return false;
		} else if (!subjectID.equals(other.subjectID))
			return false;
		return true;
	}
	
}
