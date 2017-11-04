package org.cardiacatlas.xpacs.domain.dcm4che;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class DicomAttrPatientNameHelper {
	@JsonProperty("Alphabetic")
	private String alphabetic;
	public DicomAttrPatientNameHelper() {}
	public String getAlphabetic() { return alphabetic; }
	public void setAlphabetic(String _alphabetic) { this.alphabetic = _alphabetic; }
	@Override
	public String toString() { return this.alphabetic; }
}
