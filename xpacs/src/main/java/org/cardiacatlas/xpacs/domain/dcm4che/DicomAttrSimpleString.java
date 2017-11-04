package org.cardiacatlas.xpacs.domain.dcm4che;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class DicomAttrSimpleString {
	
	private String vr;
	
	@JsonProperty("Value")
	private List<String> value;
	
	public DicomAttrSimpleString() {
		
	}
	
	public String getVr() { return this.vr; }
	public void setVr( String _vr ) { this.vr = _vr; }
	
	public List<String> getValue() { return this.value; }
	public void setValue( List<String> _value ) { this.value = _value; }
	
	@Override
	public String toString() {
		return value.size() > 0 ? value.get(0) : "N/A"; 
	}

}