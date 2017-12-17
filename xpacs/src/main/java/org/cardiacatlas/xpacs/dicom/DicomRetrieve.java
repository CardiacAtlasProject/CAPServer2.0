package org.cardiacatlas.xpacs.dicom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.dcm4che3.data.UID;
import org.dcm4che3.net.Connection;
import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.tool.getscu.GetSCU;
import org.dcm4che3.util.SafeClose;
import org.dcm4che3.util.StreamUtils;

public class DicomRetrieve extends GetSCU {
	
	public DicomRetrieve() throws IOException {
		super();
		setDefaultConnectionConfiguration();
		setDefaultServiceClass();
		
		System.out.println(this.getRemoteConnection().toString());
	}
	
	private void setDefaultConnectionConfiguration() {
		Connection conn = this.getRemoteConnection();
		conn.setReceivePDULength(Connection.DEF_MAX_PDU_LENGTH);
		conn.setSendPDULength(Connection.DEF_MAX_PDU_LENGTH);
		conn.setMaxOpsInvoked(0);
		conn.setMaxOpsPerformed(0);
//        conn.setPackPDV(true);
        conn.setConnectTimeout(0);
        conn.setRequestTimeout(0);
        conn.setAcceptTimeout(0);
        conn.setReleaseTimeout(0);
        conn.setResponseTimeout(0);
        conn.setRetrieveTimeout(0);
        conn.setIdleTimeout(0);
        conn.setSocketCloseDelay(Connection.DEF_SOCKETDELAY);
        conn.setSendBufferSize(0);
        conn.setReceiveBufferSize(0);
//        conn.setTcpNoDelay(false);
	}
	
	private void setDefaultServiceClass() throws IOException {
		String[] IVR_LE_FIRST = {
		        UID.ImplicitVRLittleEndian,
		        UID.ExplicitVRLittleEndian,
		        UID.ExplicitVRBigEndianRetired
		    };
		
		this.setInformationModel(InformationModel.PatientRoot, IVR_LE_FIRST, false);
		
		// Load store-tcs.properties
		Properties p = new Properties();
		InputStream in = StreamUtils.openFileOrURL("resource:store-tcs.properties");
		try {
			p.load(in);
		} finally {
			SafeClose.close(in);
		}
		
        for (Entry<Object, Object> entry : p.entrySet()) {
        		String[] tsuids1 = StringUtils.split((String) entry.getValue(), ';');
        		for( String tsuids2 : tsuids1 ) {
        			this.addOfferedStorageSOPClass(
        					toUID((String) entry.getKey()), 
        					toUID(tsuids2));
        		}
        }
        
	}
	
    private String toUID(String uid) {
        uid = uid.trim();
        return (uid.equals("*") || Character.isDigit(uid.charAt(0)))
                ? uid
                : UID.forName(uid);
    }
	public DicomRetrieve setCalledAET(String _aet) {
		this.getAAssociateRQ().setCalledAET(_aet);
		return this;
	}
	
	public DicomRetrieve setHostname(String _hostname) {
		this.getRemoteConnection().setHostname(_hostname);
		return this;
	}
	
	public DicomRetrieve setPort(int _port) {
		this.getRemoteConnection().setPort(_port);
		return this;
	}
	
	public DicomRetrieve setLocalHostname(String _hostname) {
		for( Connection conn : this.getDevice().getConnections() )
			conn.setHostname(_hostname);
		
		return this;
	}
	
	public DicomRetrieve setLocalPort(int _port) {
		for( Connection conn : this.getDevice().getConnections() )
			conn.setPort(_port);
		
		return this;
	}
	
	public DicomRetrieve addMatchingKey(String tagString, String value) {
		this.addKey(CLIUtils.toTag(tagString), value);
		
		return this;
	}
	
	public void execute() throws Exception {
		
        ExecutorService executorService =
                Executors.newSingleThreadExecutor();
        ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor();
		
        this.getDevice().setExecutor(executorService);
        this.getDevice().setScheduledExecutor(scheduledExecutorService);
        
        this.getAAssociateRQ().setCallingAET("GETSCU");
        
        try {
        		this.open();        		
        		this.retrieve();
        	
        } finally {
        	
        		this.close();
        		executorService.shutdown();
        		scheduledExecutorService.shutdown();
        	
        }
        
	}

}
