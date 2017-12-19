package org.cardiacatlas.xpacs.web.rest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.cardiacatlas.xpacs.dicom.DicomRetrieve;
import org.cardiacatlas.xpacs.web.rest.errors.DicomTransferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for query/retrieve to dcm4chee server using DICOM transfer syntax
 * 
 * @author Avan Suinesiaputra - 2017
 *
 */
@RestController
@RequestMapping("/api")
public class DicomResource {
	
	private final Logger log = LoggerFactory.getLogger(DicomResource.class);
	
	// settings from application.yml
	@Value("${application.pacsdb.pacs-aet}")
	private String aet;
	@Value("${application.pacsdb.pacs-hostname}")
	private String hostname;
	@Value("${application.pacsdb.pacs-port}")
	private int port;
	@Value("${application.pacsdb.tmp-dir}")
	private String tmpDir;
	
	/**
	 * Try to pick a random temporary folder under tmpDir where it stores
	 * the results from DICOM transfer and finally the zip file.
	 * 
	 * @return a new subfolder name under tmpDir
	 */
	private String createRandomSubFolderName() {
		// create a new directory under tmpDir with random alphabet
		String rndDir = RandomStringUtils.randomAlphabetic(8);
		while( Files.isDirectory(Paths.get(tmpDir,rndDir)) )
			rndDir = RandomStringUtils.randomAlphabetic(8);
		
		return rndDir;
	}
	
	/**
	 * Start DICOM transfer to store into the argument directory,
	 * zip the returned files and delete the files.
	 * 
	 * @return the newly created zip file
	 */
	private Path startDicomTransfer(String patientId, String studyInstanceUid, Path downloadStudyPath) {
		// the result zip file
		Path zipFile = Paths.get(downloadStudyPath.toString() + ".zip");		
		log.debug("Output file will be " + zipFile.toString());
		
		try {
			DicomRetrieve ret = new DicomRetrieve()
					.setCalledAET(aet)
					.setHostname(hostname)
					.setPort(port)
					.addMatchingKey("PatientID", patientId)
					.addMatchingKey("StudyInstanceUID", studyInstanceUid);
			
			ret.setStorageDirectory(downloadStudyPath.toFile());
			ret.execute();
			
			// compress the directory
			ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFile));
			
			// compress while deleting the file
			Files.walk(downloadStudyPath)
				.filter(p -> !Files.isDirectory(p))
				.forEach(p -> {
					ZipEntry zipEntry = new ZipEntry(downloadStudyPath.relativize(p).toString());
					try {
						zs.putNextEntry(zipEntry);
						Files.copy(p, zs);
						zs.closeEntry();
						
						// delete the file
						Files.delete(p);
					} catch (IOException e) {
						log.error(e.getMessage());
					}
					
				});
			
			zs.close();
			
			// delete directory
			Files.delete(downloadStudyPath);
			
		} catch( IOException e ) {
			throw DicomTransferException.raiseConnectionFailed(e.getMessage());
		} catch( Exception e ) {
			throw DicomTransferException.raiseGeneralTransferFailed(e.getMessage());
		}
		
		return zipFile;
	}
	
	/**
	 * GET /api/dicom/study?PatientID=[patientId]&StudyInstanceUID=[studyInstanceUID]
	 * 
	 * @throws IOException, Exception
	 */
	@GetMapping("/dicom/study")
	public Map<String,Object> dicomDownloadStudy(String patientId, String studyInstanceUid)  {
		log.info("Request to download study [PatientID={}, StudyInstanceUID={}]", patientId, studyInstanceUid);
		
		if( patientId==null || studyInstanceUid==null )
			throw DicomTransferException.raiseMissingUriParameters("Required PatientID and StudyInstanceUID in the URI.");
		
		String rndDir = createRandomSubFolderName();
		
		// create directory
		Path downloadStudyPath = Paths.get(tmpDir, rndDir, studyInstanceUid);
		try {
			Files.createDirectories(downloadStudyPath);
		} catch( IOException e ) {
			throw DicomTransferException.raiseFileSystemIO("Cannot create directory: " + downloadStudyPath.toString());
		}
		
		// start transfer
		Path zipFile = startDicomTransfer(patientId, studyInstanceUid, downloadStudyPath);
		if( !Files.exists(zipFile) )
			throw DicomTransferException.raiseFileSystemIO("Cannot create output ZIP file");
			
		// prepare result
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("folder", rndDir);
		return result;
			
	}
	
	
	/**
	 * GET api/dicom/download/:folder?filename=[filename]
	 * 
	 */
	@GetMapping("/dicom/download/{folder}")
	public void dicomDownloadFile(HttpServletResponse response, @PathVariable String folder, String filename) {
		
		// create file for transfer
		File file = new File(Paths.get(tmpDir, folder, filename).toAbsolutePath().toString());
		if( !file.exists() ) {
			throw DicomTransferException.raiseFileSystemIO("Not Found: " + file.toString());
		}

		log.info("Request to download {}", file.toString());

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
		response.setContentLength((int) file.length());
		response.addHeader("x-filename", filename);

		try {
		
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, response.getOutputStream());
			
		} catch (Exception e) {
			throw DicomTransferException.raiseFileSystemIO("Cannot download " + filename);
		} finally {
			// delete files here
			try {
				Files.delete(file.toPath());
				Files.delete(Paths.get(tmpDir, folder));
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
 	}


}
