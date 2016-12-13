package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.service.PatientInfoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private final PatientInfoRepository patientInfoRepository;
	
	public UserController(PatientInfoRepository patientInfoRepository) {
		this.patientInfoRepository = patientInfoRepository;
	}
	
	@GetMapping
	public ModelAndView patientsListAll() {
		Iterable<PatientInfo> allPatients = patientInfoRepository.findAll();
		
		return new ModelAndView("user/patients", "patients", allPatients);
	}
	
	
}
