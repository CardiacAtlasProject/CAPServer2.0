package org.cardiacatlas.xpacs.domain.dcm4che;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class DicomAttrPatientName {
	
	private String vr;
	
	@JsonProperty("Value")
	private List<DicomAttrPatientNameHelper> value;
	
	public String getVr() { return this.vr; }
	public void setVr( String _vr ) { this.vr = _vr; }
	
	public List<DicomAttrPatientNameHelper> getValue() { return this.value; }
	public void setValue( List<DicomAttrPatientNameHelper> _value ) { this.value = _value; }
	
	@Override
	public String toString() {
		return value.size() > 0 ? value.get(0).toString() : "N/A";
	}

}
