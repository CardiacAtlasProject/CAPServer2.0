package nz.auckland.abi.webservices;

import java.io.Serializable;
import java.util.Arrays;

public class CAPStudyMetaData implements Serializable {

	private static final long serialVersionUID = -8246387004486474376L;

	String studyID;

	String descriptor;

	String filename;

	byte[] data;

	@Override
	public String toString() {
		return "CAPStudyMetaData [studyID=" + studyID + ", descriptor=" + descriptor + ", filename=" + filename + ", data=" + Arrays.toString(data) + "]";
	}
}
