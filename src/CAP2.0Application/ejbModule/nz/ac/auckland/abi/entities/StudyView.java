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
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity(name = "StudyView")
public class StudyView implements Serializable{
	private static final long serialVersionUID = 6998L;
	
	@Id
    @Column(name = "subject_id")
    private String id;
    
	@Column(name = "subject_name")
    private String name;

    @Column(name = "subject_birthdate")
    private String birthdate;

    @Column(name = "subject_gender")
    private String gender;

    @Id
	@Column(name = "study_id")
	private String studyID;
	
	@Column(name = "study_modalities")
	private String studyModalities;

	@Column(name = "study_date")
	private java.sql.Timestamp studyDate;

	@Column(name = "study_description")
	private String studyDescription;
	
	@Column(name = "study_metadata")
	@Lob
	private byte[] studyMetaData;
	
	@Column(name = "study_json")
	@Lob
	private byte[] json;
	
	
	public StudyView(){
		super();
	}
	
	public StudyView(String id, String name, String dob, String gender, String sid, String sModalities, String sDate,
			String sDes) throws Exception{
		this.id = id;
		this.name = name;
		this.birthdate = dob;
		this.gender = gender;
		studyID = sid;
		studyDescription = sDes;
		studyModalities = sModalities;
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
    
    public String getId() {
 		return id;
 	}

 	public void setId(String id) {
 		this.id = id;
 	}

 	public String getName() {
 		return name;
 	}

 	public void setName(String name) {
 		this.name = name;
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
    
 	public void setGender(String sex) {
 		this.gender = sex;
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

	public void setStudyModalities(String sMod) {
		this.studyModalities = sMod;
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

	public byte[] getStudyMetaData() {
		return studyMetaData;
	}

	public void setStudyMetaData(byte[] studyMetaData) {
		this.studyMetaData = studyMetaData;
	}

	public String getJson() {
		return new String(json,Charset.forName("UTF-8"));
	}

	public void setJson(byte[] json) {
		this.json = json;
	}
 	
 		
 	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((studyID == null) ? 0 : studyID.hashCode());
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
		StudyView other = (StudyView) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (studyID == null) {
			if (other.studyID != null)
				return false;
		} else if (!studyID.equals(other.studyID))
			return false;
		return true;
	}

	@Override
 	public String toString(){
 		return "Subject "+name+" has id "+id+" was born on "+birthdate+" and is a "+gender+" Study :" + id+ "\t" + studyID + "\t" + studyDescription
				+ "\t" + studyDate;
 	}
 	
}
