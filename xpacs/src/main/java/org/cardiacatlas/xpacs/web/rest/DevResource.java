package org.cardiacatlas.xpacs.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.domain.enumeration.GenderType;
import org.cardiacatlas.xpacs.repository.AuxFileRepository;
import org.cardiacatlas.xpacs.repository.BaselineDiagnosisRepository;
import org.cardiacatlas.xpacs.repository.CapModelRepository;
import org.cardiacatlas.xpacs.repository.ClinicalNoteRepository;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * Controller for special dev menu
 *
 */
@RestController
@RequestMapping("/api")
public class DevResource {

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
				.patientId("PAT003")
				.cohort("GROUP XX")
				.ethnicity("Asian")
				.gender(GenderType.male));
		patientInfoRepository.save(newPats);
		
		
		return ResponseEntity.created(new URI("/api/populate-tables"))
				.body("Tables are populated with dummy data.");
	}
	
}	
