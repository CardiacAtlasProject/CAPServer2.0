package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.service.PatientHistoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PatientHistoryController {
	
	private final PatientHistoryRepository patientHistoryRepository;
	
	public PatientHistoryController(PatientHistoryRepository _patientHistoryRepository) {
		this.patientHistoryRepository = _patientHistoryRepository;
	}
	
	@GetMapping("/user/show_patient_history")
	public ModelAndView patientHistoryAll() {
		return new ModelAndView("/user/patient_history_table", "pathists", patientHistoryRepository.findAll());
	}

}
