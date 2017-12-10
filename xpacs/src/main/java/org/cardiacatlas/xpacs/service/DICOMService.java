package org.cardiacatlas.xpacs.service;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.cardiacatlas.xpacs.domain.DICOM;
import org.cardiacatlas.xpacs.repository.DICOMRepository;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.UserIdentity;
import org.dcm4che2.tool.dcmqr.DcmQR;
import org.dcm4che2.tool.dcmsnd.DcmSnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing DICOM.
 */
@Service
@Transactional
public class DICOMService {

	private final Logger log = LoggerFactory.getLogger(DICOMService.class);

	private final DICOMRepository dICOMRepository;

	public DICOMService(DICOMRepository dICOMRepository) {
		this.dICOMRepository = dICOMRepository;
	}
	
	private void reportException(Exception e){
		try {
			File string = File.createTempFile("tmpstring", null, null);
		
			PrintStream stream = new PrintStream(string);
			e.printStackTrace(stream);
			InputStream is = new FileInputStream(string);
			BufferedReader buf = new BufferedReader(new InputStreamReader(is));
			String line = buf.readLine();
			StringBuilder sb = new StringBuilder();
			while(line != null){
			   sb.append(line).append("\n");
			   line = buf.readLine();
			}
			buf.close();
			log.error(sb.toString());
		} catch (IOException e1) {
			reportException(e1);
		}
	}

	/**
	 * transform a DICOM file to a byte array
	 * 
	 * http://forums.dcm4che.org/jiveforums/thread.jspa?threadID=2611
	 * 
	 * @param obj
	 *            object to be transformed
	 * @return byte array of object
	 * @throws IOException
	 */
	private byte[] dicomtoByteArray(DicomObject obj) throws IOException {
		byte[] data = new byte[0];
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DicomOutputStream dos = new DicomOutputStream(new BufferedOutputStream(baos));
			dos.writeDicomObject(obj, dos.getTransferSyntax());
			dos.flush();
			dos.close();
			
			data = baos.toByteArray();
		} catch(Exception e){
			reportException(e);
		}
		return data;
	}

	/**
	 * read DICOM byte array's image
	 * http://forums.dcm4che.org/jiveforums/thread.jspa?threadID=2611
	 * 
	 * @param dicomData
	 * @return bufferedimage of dicom
	 * @throws IOException
	 */
	private static BufferedImage getPixelDataAsBufferedImage(byte[] dicomData) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(dicomData);
		BufferedImage buff = null;
		Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
		ImageReader reader = (ImageReader) iter.next();
		DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
		ImageInputStream iis = ImageIO.createImageInputStream(bais);
		reader.setInput(iis, false);
		buff = reader.read(0, param);
		iis.close();
		if (buff == null)
			throw new IOException("Could not read Dicom file. Maybe pixel data is invalid.");
		return buff;
	}

	/**
	 * Save a dICOM.
	 *
	 * @param dICOM
	 *            the entity to save
	 * @return the persisted entity
	 */
	public DICOM save(DICOM dICOM) {
		log.debug("Request to save DICOM : {}", dICOM);

		File tmpupload = null;
		FileOutputStream fileoutstream;
		try {
			tmpupload = File.createTempFile("tmpupload", "dcm", null);
			fileoutstream = new FileOutputStream(tmpupload);
			fileoutstream.write(dICOM.getDicomFile());
			fileoutstream.flush();
			fileoutstream.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		DcmSnd dcmsnd = new DcmSnd("DCM4CHEE");
		dcmsnd.setCalledAET("DCM4CHEE");
		dcmsnd.setRemoteHost("127.0.0.1");
		dcmsnd.setRemotePort(11112);
		dcmsnd.setStorageCommitment(true);
		UserIdentity userId = new UserIdentity.UsernamePasscode("admin", "admin".toCharArray());
		dcmsnd.setUserIdentity(userId);
		dcmsnd.addFile(tmpupload);
		dcmsnd.configureTransferCapability();
		try {
			dcmsnd.start();
			dcmsnd.open();
			log.info("Connected to remote");
			dcmsnd.send();
			dcmsnd.commit();
			dcmsnd.close();
			log.info("Released connection to remote");
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			dcmsnd.stop();
		}

		dICOM.setDicomFile(new byte[0]);
		return dICOMRepository.save(dICOM);
	}

	/**
	 * Get all the dICOMS.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<DICOM> findAll(Pageable pageable) {
		log.debug("Request to get all DICOMS");

		Page<DICOM> page = null;

		DcmQR dcmqr = new DcmQR("DCM4CHEE");
		dcmqr.setCalledAET("DCM4CHEE", false);
		dcmqr.setRemoteHost("127.0.0.1");
		dcmqr.setRemotePort(11112);
		UserIdentity userId = new UserIdentity.UsernamePasscode("admin", "admin".toCharArray());
		dcmqr.setUserIdentity(userId);
		dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.PATIENT);
		dcmqr.addMatchingKey(Tag.toTagPath("PatientID"), "*");
		dcmqr.setCGet(true);
		//dcmqr.setStoreDestination("/tmp");
		String tsuids[] = {"1.2.840.10008.5.1.4.1.2.2.2"};
        //dcmqr.addStoreTransferCapability(UID.CTImageStorage, tsuids);

		dcmqr.configureTransferCapability(true);

		try {
			dcmqr.start();
			log.info("started");
			dcmqr.open();
			log.info("opened");
			List<DicomObject> result = dcmqr.query();
			log.info("move");
			dcmqr.get(result);
			//dcmqr.move(result);

			List<DICOM> listOfDICOMs = new ArrayList<DICOM>();
			dcmqr.close();
			for (int i = 0; i < result.size(); i++) {
				log.info("DICOM OBJECT: " + result.get(i).toString());
				DICOM dICOM = new DICOM();
				dICOM.setDicomFile(dicomtoByteArray(result.get(i)));
				dICOM.setId((long) i);

				/*
				ByteArrayOutputStream image = new ByteArrayOutputStream();
				ImageIO.write(getPixelDataAsBufferedImage(dICOM.getDicomFile()), "jpg", image);
				image.flush();
				byte[] imageInByte = image.toByteArray();
				image.close();
				dICOM.setImage(imageInByte);
				*/

				listOfDICOMs.add(dICOM);
			}

			page = new PageImpl<DICOM>(listOfDICOMs, pageable, listOfDICOMs.size());
			log.info("Finish result found " + result.size() + " dicom objects.");

		} catch (Exception e) {
			reportException(e);
		} finally {
			dcmqr.stop();
		}

		return page;// dICOMRepository.findAll(pageable);
	}

	/**
	 * Get one dICOM by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public DICOM findOne(Long id) {
		log.debug("Request to get DICOM : {}", id);

		DICOM dICOM = new DICOM();

		DcmQR dcmqr = new DcmQR("DCM4CHEE");
		dcmqr.setCalledAET("DCM4CHEE", false);
		dcmqr.setRemoteHost("127.0.0.1");
		dcmqr.setRemotePort(11112);
		UserIdentity userId = new UserIdentity.UsernamePasscode("admin", "admin".toCharArray());
		dcmqr.setUserIdentity(userId);
		dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.PATIENT);
		dcmqr.addMatchingKey(Tag.toTagPath("PatientID"), "*");
		dcmqr.setCGet(true);
		dcmqr.configureTransferCapability(true);

		try {
			dcmqr.start();
			log.info("started");
			dcmqr.open();
			log.info("opened");
			List<DicomObject> result = dcmqr.query();
			log.info("get");
			dcmqr.get(result);
			dcmqr.close();
			log.info("DICOM OBJECT: " + result.get(java.lang.Math.toIntExact(id)).toString());
			dICOM.setDicomFile(dicomtoByteArray(result.get(java.lang.Math.toIntExact(id))));
			dICOM.setId(id);
			/*
			ByteArrayOutputStream image = new ByteArrayOutputStream();
			ImageIO.write(getPixelDataAsBufferedImage(dICOM.getDicomFile()), "jpg", image);
			image.flush();
			byte[] imageInByte = image.toByteArray();
			image.close();
			dICOM.setImage(imageInByte);
			*/
			
			log.info("Finish result found " + result.size() + " dicom objects.");
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			dcmqr.stop();
		}

		;
		return dICOM;
	}

	/**
	 * Delete the dICOM by id.
	 *
	 * @param id
	 *            the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete DICOM : {}", id);
		dICOMRepository.delete(id);
	}
}
