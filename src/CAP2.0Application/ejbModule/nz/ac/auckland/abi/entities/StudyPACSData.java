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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity(name = "StudyPACSData")
public class StudyPACSData implements Serializable {
	private static final long serialVersionUID = 7L;


	@Id
	@Column(name = "study_id")
	private String studyID;
	
		
	@Column(name = "study_pacsdata")
	@Lob
	private byte[] studyPACSData;
	
	
	public StudyPACSData() {
		super();
	}

	public StudyPACSData(String sid) throws Exception {
		super();
		studyID = sid;
	}


	public String getStudyID() {
		return studyID;
	}

	public void setStudyID(String studyID) {
		this.studyID = studyID;
	}

	public byte[] getStudyPACSData() {
		return studyPACSData;
	}

	public void setStudyPACSData(byte[] studyPACSData) {
		this.studyPACSData = studyPACSData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		StudyPACSData other = (StudyPACSData) obj;
		if (studyID == null) {
			if (other.studyID != null)
				return false;
		} else if (!studyID.equals(other.studyID))
			return false;
		return true;
	}

	
}
