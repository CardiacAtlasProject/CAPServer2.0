package nz.ac.auckland.abi.dcm4chee;
import java.util.Properties;

//Tested with dcm4chee-tool-dcmqr-2.0.23
import org.dcm4che2.tool.dcmqr.DcmQR;

public class RetriveStudyFromPACS {
	private String localAET = "DCMQR";
	private String localHost = "127.0.0.1";
	private String localPort = "104";
	
	private String remoteAET = "DCM4CHEE";
	private String remoteHost = "127.0.0.1";
	private String remotePort = "11112";
	
	private String destination = ".";
	private String studyid;
	private String getModalities;
	
	public RetriveStudyFromPACS(Properties pacsAccessProperties){
    	localAET = pacsAccessProperties.getProperty("CAET", "DCMQR");
    	localPort = pacsAccessProperties.getProperty("CAETPort","104");
    	localHost = pacsAccessProperties.getProperty("CAETHost","127.0.0.1");
    	remoteAET = pacsAccessProperties.getProperty("AET","DCM4CHEE");
    	remotePort = pacsAccessProperties.getProperty("AETPort","11112");
    	remoteHost = pacsAccessProperties.getProperty("AETHost","127.0.0.1");
    	getModalities = pacsAccessProperties.getProperty("Modalities","MR");
	}
	
	public void retriveFilesTo(String DEST) throws Exception{
		this.destination = DEST;
		String query = "-L "+this.localAET+"@"+this.localHost+":"+this.localPort+" "+
						this.remoteAET+"@"+this.remoteHost+":"+this.remotePort+" -qStudyInstanceUID="+
						this.studyid+" -cget -cstore "+ this.getModalities+" -cstoredest "+this.destination;
		String input[] = query.split(" ");
		//System.out.println(query);
		DcmQR.main(input);
	}

	public String getLocalAET() {
		return localAET;
	}

	public void setLocalAET(String localAET) {
		this.localAET = localAET;
	}

	public String getLocalHost() {
		return localHost;
	}

	public void setLocalHost(String localHost) {
		this.localHost = localHost;
	}

	public String getLocalPort() {
		return localPort;
	}

	public void setLocalPort(String localPort) {
		this.localPort = localPort;
	}

	public String getRemoteAET() {
		return remoteAET;
	}

	public void setRemoteAET(String remoteAET) {
		this.remoteAET = remoteAET;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getStudyid() {
		return studyid;
	}

	public void setStudyid(String studyid) {
		this.studyid = studyid;
	}

	public String getModalities() {
		return getModalities;
	}

	public void setModalities(String getModalities) {
		this.getModalities = getModalities;
	}
	
}
