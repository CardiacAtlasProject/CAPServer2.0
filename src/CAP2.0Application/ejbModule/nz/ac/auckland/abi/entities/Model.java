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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity(name = "Model")
public class Model implements Serializable{
	private static final long serialVersionUID = 8L;
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY ) 
	@Column(name = "model_id")
	private Long modelID;
	

	@Column(name = "study_id")
	private String studyID;
		
	@Column(name = "model_name")
	private String modelName;
	
	@Column(name = "model_comments")
	private String modelComments;	
	
	@Column(name = "model_metadata")
	@Lob
	private byte[]  modelMetadata;
		
	@Column(name = "model_xml")
	@Lob
	private byte[] modelXml;
	
	@Column(name = "model_exarchive")
	@Lob
	private byte[] modelExArchive;
	
	@Column(name = "model_vtparchive")
	@Lob
	private byte[] modelVtpArchive;
	
	
	
	public Model(){
		super();
	}
	
	public Model(String studyid, String modelName, String modelComments){
		super();
		this.studyID = studyid;
		this.modelName = modelName;
		this.modelComments = modelComments;
	}
		
	public void setModelID(Long modelID) {
		this.modelID = modelID;
	}

	public long getModelID() {
		return modelID;
	}

	public String getStudyID() {
		return studyID;
	}

	public void setStudyID(String studyID) {
		this.studyID = studyID;
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

	public byte[] getModelMetadata() {
		return modelMetadata;
	}

	public void setModelMetadata(byte[] modelMetadata) {
		this.modelMetadata = modelMetadata;
	}

	public byte[] getModelXml() {
		return modelXml;
	}

	public void setModelXml(byte[] modelXml) {
		this.modelXml = modelXml;
	}

	public byte[] getModelExArchive() {
		return modelExArchive;
	}

	public void setModelExArchive(byte[] modelExArchive) {
		this.modelExArchive = modelExArchive;
	}

	public byte[] getModelVtpArchive() {
		return modelVtpArchive;
	}

	public void setModelVtpArchive(byte[] modelVtpArchive) {
		this.modelVtpArchive = modelVtpArchive;
	}

}
