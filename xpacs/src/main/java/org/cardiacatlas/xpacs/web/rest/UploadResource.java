package org.cardiacatlas.xpacs.web.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import org.cardiacatlas.xpacs.domain.enumeration.GenderType;
import javax.validation.Valid;

import org.cardiacatlas.xpacs.domain.BaselineDiagnosis;
import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.AuxFileRepository;
import org.cardiacatlas.xpacs.repository.BaselineDiagnosisRepository;
import org.cardiacatlas.xpacs.repository.CapModelRepository;
import org.cardiacatlas.xpacs.repository.ClinicalNoteRepository;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.cardiacatlas.xpacs.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

@RestController
@RequestMapping("/upload")
public class UploadResource {
	private final PatientInfoRepository patientInfoRepository;
    private final ClinicalNoteRepository clinicalNoteRepository;
    private final BaselineDiagnosisRepository baselineDiagnosisRepository;
    
    public UploadResource(PatientInfoRepository patientInfoRepository,ClinicalNoteRepository clinicalNoteRepository,BaselineDiagnosisRepository baselineDiagnosisRepository) {
        this.patientInfoRepository = patientInfoRepository;
        this.clinicalNoteRepository = clinicalNoteRepository;
        this.baselineDiagnosisRepository =  baselineDiagnosisRepository;
    }
	private final Logger log = LoggerFactory.getLogger(PatientInfoResource.class);
	//Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "downloads/";
    private static String SCRIPTS_FOLDER = "scripts/";
	@SuppressWarnings({ "deprecation", "unchecked" })
	@PostMapping("/status")
    @Timed
  //Single file upload
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadfile,@RequestParam("UploadEntity") String UploadEntity) {

        
        log.debug("Uploading entity "+UploadEntity);
        if (uploadfile.isEmpty()) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }
        
//        if(!(uploadfile.getOriginalFilename().split(".")[1].equals("csv"))){
//        	return new ResponseEntity("please upload a csv file only!", HttpStatus.BAD_REQUEST);
//        }

        
        try {
            saveUploadedFiles(Arrays.asList(uploadfile));
	        if(UploadEntity.equals("patient_info")){
	            uploadPatientInfo(uploadfile.getOriginalFilename());
	        }
	        if(UploadEntity.equals("clinical_note")){
	            uploadClinicalNoteInfo(uploadfile.getOriginalFilename());
	        }
	        if(UploadEntity.equals("baseline_diagnosis")){
	            uploadBaselineDiagnosisInfo(uploadfile.getOriginalFilename());
	        }
        } catch (IOException e) {
        	new File(System.getProperty("user.dir")+"/downloads/"+uploadfile.getOriginalFilename()).delete();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        new File(System.getProperty("user.dir")+"/downloads/"+uploadfile.getOriginalFilename()).delete();
        return new ResponseEntity("Successfully uploaded - " +
                uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);
	}
	
	public boolean uploadPatientInfo(String filename){
        
        String s = null;
		final String FILE_NAME = System.getProperty("user.dir")+"/downloads/"+filename;
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
	    try{
            br = new BufferedReader(new FileReader(FILE_NAME));
            List<PatientInfo> patientInfos = new ArrayList<PatientInfo>();
            PatientInfo patientInfo;
            int count=0;
            while ((line = br.readLine()) != null) {
            	//skip reading first row
            	if (count==0){
            		count+=1;
            		continue;
            	}
            	patientInfo=new PatientInfo();
                // use comma as separator
                String[] info = line.split(cvsSplitBy);
                patientInfo.setPatient_id(info[0]);
                patientInfo.setCohort(info[1]);
                patientInfo.setEthnicity(info[2]);
                String gender = info[3];
            	if(gender.toLowerCase().equals("male"))
                    patientInfo.setGender(GenderType.male);
            	else
            		patientInfo.setGender(GenderType.female);
            	patientInfo.setPrimary_diagnosis(info[4]);
                patientInfos.add(patientInfo);
                
            }
            
			createPatientInfo(patientInfos); 
        } catch (FileNotFoundException e) {
            log.debug(e.toString());
        } catch (IOException e) {
        	log.debug(e.toString());
        }
	    catch (URISyntaxException e) {
	    	log.debug(e.toString());
		} finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	    
        return true;

    }
	
	
	public boolean uploadClinicalNoteInfo(String filename){
        
        String s = null;
		final String FILE_NAME = System.getProperty("user.dir")+"/downloads/"+filename;
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
	    try{
            br = new BufferedReader(new FileReader(FILE_NAME));
            
            List<ClinicalNote> clinicalNotes = new ArrayList<ClinicalNote>();
            ClinicalNote clinicalNote;
            int count=0;
            while ((line = br.readLine()) != null) {
            	if (count==0){
            		count+=1;
            		continue;
            	}
            	clinicalNote=new ClinicalNote();
                // use comma as separator
                String[] info = line.split(cvsSplitBy);
                
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
                final LocalDate dt = LocalDate.parse(info[0],dtf);
                
                clinicalNote.setAssessment_date(dt);
                clinicalNote.setAge(Float.valueOf(info[1]));
                clinicalNote.setHeight(info[2]);
                clinicalNote.setWeight(info[3]);
                clinicalNote.setDiagnosis(info[4]);
            	clinicalNote.setNote(info[5]);
            	
            	Long id = patientInfoRepository.findID(info[6]);
            	
            	clinicalNote.setPatientInfoFK(patientInfoRepository.findOne(id));
            	clinicalNotes.add(clinicalNote);
                
            }
            
			createClinicalNote(clinicalNotes); 
        } catch (FileNotFoundException e) {
            log.debug(e.toString());
        } catch (IOException e) {
        	log.debug(e.toString());
        }
	    catch (DateTimeParseException e) {
	        log.debug(e.toString());
	    }
	    catch (URISyntaxException e) {
	    	log.debug(e.toString());
		} finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	    
        return true;

    }
	
	public boolean uploadBaselineDiagnosisInfo(String filename){
        
        String s = null;
		final String FILE_NAME = System.getProperty("user.dir")+"/downloads/"+filename;
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
	    try{
            br = new BufferedReader(new FileReader(FILE_NAME));
            
            List<BaselineDiagnosis> baselineDiagnosis = new ArrayList<BaselineDiagnosis>();
            BaselineDiagnosis baselineDiag;
            int count=0;
            while ((line = br.readLine()) != null) {
            	if (count==0){
            		count+=1;
            		continue;
            	}
            	baselineDiag=new BaselineDiagnosis();
                // use comma as separator
                String[] info = line.split(cvsSplitBy);
                
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy");
                final LocalDate dt = LocalDate.parse(info[0],dtf);
                
                baselineDiag.setDiagnosis_date(dt);
                baselineDiag.setAge(Float.valueOf(info[1]));
                baselineDiag.setHeight(info[2]);
                baselineDiag.setWeight(info[3]);
                baselineDiag.setHeart_rate(info[4]);
                baselineDiag.setDbp(info[5]);
                baselineDiag.setSbp(info[6]);
                baselineDiag.setHistory_of_alcohol(info[7]);
                baselineDiag.setHistory_of_diabetes(info[8]);
                baselineDiag.setHistory_of_hypertension(info[9]);
                baselineDiag.setHistory_of_smoking(info[10]);
            	Long id = patientInfoRepository.findID(info[11]);
            	baselineDiag.setPatientInfoFK(patientInfoRepository.findOne(id));
            	baselineDiagnosis.add(baselineDiag);
                
            }
            
			createBaselineDiagnosis(baselineDiagnosis); 
        } catch (FileNotFoundException e) {
            log.debug(e.toString());
        } catch (IOException e) {
        	log.debug(e.toString());
        }
	    catch (DateTimeParseException e) {
	        log.debug(e.toString());
	    }
	    catch (URISyntaxException e) {
	    	log.debug(e.toString());
		} finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	    
        return true;

    }
	
	public ResponseEntity<String> createPatientInfo(List<PatientInfo> patientInfo) throws URISyntaxException {
		final String ENTITY_NAME = "patientInfo";
		String result = "Success!";
		for(PatientInfo patInfo : patientInfo){
            log.debug("REST request to save PatientInfo : {}", patientInfo);
            try{
                patientInfoRepository.save(patientInfo);
            }
            catch(Exception e){
            	result = "Failed to upload the csv file! Make sure there are no duplicate patient IDs";
            	break;
            }
		}
        return ResponseEntity.created(new URI("/api/patient-infos/"))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result))
            .body(result);
    }
	
	public ResponseEntity<String> createClinicalNote(List<ClinicalNote> clinicalNotes) throws URISyntaxException {
		final String ENTITY_NAME = "clinicalNote";
		String result = "Success!";
		for(ClinicalNote clinicalNote : clinicalNotes){
			try{
	            clinicalNoteRepository.save(clinicalNote);
			}
			catch(Exception e){
            	result = "Failed to upload the csv file! Make sure there are no duplicate patient IDs";
            	break;
            }
	        
		}
		return ResponseEntity.created(new URI("/api/clinical-notes/"))
	            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result))
	            .body(result);
    }
	
	public ResponseEntity<String> createBaselineDiagnosis(List<BaselineDiagnosis> baselineDiagnosis) throws URISyntaxException {
		final String ENTITY_NAME = "baselineDiagnosis";
		String result = "Success!";
		for(BaselineDiagnosis baselineDiag : baselineDiagnosis){
			try{
				baselineDiagnosisRepository.save(baselineDiag);
			}
			catch(Exception e){
            	result = "Failed to upload the csv file! Make sure there are no duplicate patient IDs";
            	break;
            }
	        
		}
		return ResponseEntity.created(new URI("/api/clinical-notes/"))
	            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result))
	            .body(result);
    }
	
	//save file
    private void saveUploadedFiles(List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; 
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            System.out.println("Path = "+path);
            Files.write(path, bytes);

        }

    }
}
