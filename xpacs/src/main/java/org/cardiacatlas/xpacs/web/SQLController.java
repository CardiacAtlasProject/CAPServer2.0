package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.service.PatientInfoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sql")
public class SQLController {

	private final PatientInfoRepository patientInfoRepository;
	
	public SQLController(PatientInfoRepository patientInfoRepository) {
		this.patientInfoRepository = patientInfoRepository;
	}
	
	@GetMapping("show_patient_info")
	public ModelAndView patientsListAll() {
		Iterable<PatientInfo> allPatients = patientInfoRepository.findAll();
		
		return new ModelAndView("sql/patient_info_table", "patients", allPatients);
	}
}
