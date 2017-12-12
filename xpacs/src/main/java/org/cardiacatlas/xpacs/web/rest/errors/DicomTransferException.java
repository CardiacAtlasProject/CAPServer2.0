package org.cardiacatlas.xpacs.web.rest.errors;

/**
 * Exception caused during DICOM transfer.
 * 
 * 
 * 
 * @author Avan Suinesiaputra - 2017
 *
 */
public class DicomTransferException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

    public enum ExceptionType {
    		CONNECTION_FAILED("error.ConnectionFailed"),
    		MISSING_URI_PARAMETERS("error.MissingUriParameters"),
    		TRANSFER_FAILED("error.DicomTransferFailed"),
    		FILESYSTEM_IO_ERROR("error.FileSystemIO");
    	
    		private final String errorTypeMsg;
    		
    		ExceptionType(String _type) {
    			errorTypeMsg = _type;
    		}
    		
    		public String getTypeMsg() { return errorTypeMsg; }
    }
    
    private final ExceptionType type;
    private final String description;
    
    public DicomTransferException(ExceptionType _type, String _msg) {
    		super(_msg);
    		this.type = _type;
    		this.description = _msg;
    }
    
    public ErrorVM getErrorVM() {
    		return new ErrorVM(type.getTypeMsg(), description);
    }
    
    // -- Factory
    public static DicomTransferException raiseConnectionFailed(String _msg) {
    		return new DicomTransferException(DicomTransferException.ExceptionType.CONNECTION_FAILED, _msg);
    }
    
    public static DicomTransferException raiseGeneralTransferFailed(String _msg) {
    		return new DicomTransferException(DicomTransferException.ExceptionType.TRANSFER_FAILED, _msg);
    }

    public static DicomTransferException raiseMissingUriParameters(String _msg) {
		return new DicomTransferException(DicomTransferException.ExceptionType.MISSING_URI_PARAMETERS, _msg);
    }

    public static DicomTransferException raiseFileSystemIO(String _msg) {
		return new DicomTransferException(DicomTransferException.ExceptionType.FILESYSTEM_IO_ERROR, _msg);
    }
    
}
