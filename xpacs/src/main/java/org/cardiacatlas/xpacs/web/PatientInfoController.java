package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.service.PatientInfoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PatientInfoController {
	
	private final PatientInfoRepository patientInfoRepository;
	
	public PatientInfoController(PatientInfoRepository _patientInfoRepository) {
		this.patientInfoRepository = _patientInfoRepository;
	}
	
	@GetMapping("/user/show_patient_info")
	public ModelAndView patientsListAll() {
		return new ModelAndView("/user/patient_info_table", "patients", patientInfoRepository.findAll());
	}

	@GetMapping("/user/manage_patients")
	public ModelAndView managePatients() {
		return new ModelAndView("/user/manage_patients", "patients", patientInfoRepository.findAll());
	}
	
}
