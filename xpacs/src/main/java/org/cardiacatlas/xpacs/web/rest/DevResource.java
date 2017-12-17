package org.cardiacatlas.xpacs.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.cardiacatlas.xpacs.dicom.PacsJdbcTemplate;
import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.domain.enumeration.GenderType;
import org.cardiacatlas.xpacs.repository.AuxFileRepository;
import org.cardiacatlas.xpacs.repository.BaselineDiagnosisRepository;
import org.cardiacatlas.xpacs.repository.CapModelRepository;
import org.cardiacatlas.xpacs.repository.ClinicalNoteRepository;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * Controller for special dev menu
 * This REST functions do not show on the menu, but you can run these
 * usinig Admin -> SWAGGER UI
 *
 */
@RestController
@RequestMapping("/api")
public class DevResource {
	
	private static final Logger log = LoggerFactory.getLogger(DevResource.class);

    private final PatientInfoRepository patientInfoRepository;
    private final ClinicalNoteRepository clinicalNoteRepository;
    public DevResource(PatientInfoRepository patientInfoRepository,ClinicalNoteRepository clinicalNoteRepository,BaselineDiagnosisRepository baselineDiagnosisRepository) {
        this.patientInfoRepository = patientInfoRepository;
        this.clinicalNoteRepository = clinicalNoteRepository;
    }
	
	
	/**
	 * GET /populate-tables: populate tables with dummy data (create rows)
	 * 
	 * @return a string with status 201 (Created)
	 */
	@GetMapping("/populate-tables")
	public ResponseEntity<String> populateTables() throws URISyntaxException {
		
		ArrayList<PatientInfo> newPats = new ArrayList<PatientInfo>();
		newPats.add(new PatientInfo()
				.patientId("PAT001")
				.cohort("GROUP 01")
				.ethnicity("Asian")
				.gender(GenderType.female)
				.primaryDiagnosis("Healthy volunteers"));
		newPats.add(new PatientInfo()
				.patientId("PAT002")
				.cohort("GROUP 01")
				.ethnicity("European")
				.gender(GenderType.male)
				.primaryDiagnosis("Diagnosed with HEFrEF"));
		newPats.add(new PatientInfo()
				.patientId("SCD0003001")
				.cohort("Sunnybrook Cardiac Data")
				.ethnicity("UNKNOWN")
				.gender(GenderType.unknown)
				.primaryDiagnosis("Myocardial infarction"));
		newPats.add(new PatientInfo()
				.patientId("PAT003")
				.cohort("GROUP XX")
				.ethnicity("Asian")
				.gender(GenderType.male));
		newPats.add(new PatientInfo()
				.patientId("SCD0003401")
				.cohort("Sunnybrook Cardiac Data")
				.gender(GenderType.male));
		patientInfoRepository.save(newPats);
		
		
		return ResponseEntity.created(new URI("/api/populate-tables"))
				.body("Tables are populated with dummy data.");
	}
	
	@Autowired
	PacsJdbcTemplate pacsConn;
	
	
	/**
	 * GET /pacs-jdbc-connection: test SQL connection to the pacs database
	 * 
	 *  @return a string with status 201 (Created)
	 */
	@GetMapping("/pacs-jdbc-connection")
	public ResponseEntity<String> pacsJdbcConnection() throws URISyntaxException {
		
		String tables = pacsConn.showTables()
			.stream()
			.collect(Collectors.joining(", "));

		String content = "PACS table= " + tables;

		return ResponseEntity.created(new URI("/api/pacs-jdbc-connection"))
				.body(content);
	}
	
}	
