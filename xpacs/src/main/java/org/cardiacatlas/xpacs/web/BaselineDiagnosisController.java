package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.service.BaselineDiagnosisRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BaselineDiagnosisController {
	
	private final BaselineDiagnosisRepository baselineDiagnosisRepository;
	
	public BaselineDiagnosisController(BaselineDiagnosisRepository _baselineDiagnosisRepository) {
		this.baselineDiagnosisRepository = _baselineDiagnosisRepository;
	}
	
	@GetMapping("/user/show_baseline_diagnosis")
	public ModelAndView showAllBaselineDiagnosis() {
		return new ModelAndView("/user/baseline_diagnosis_table", "diags", this.baselineDiagnosisRepository.findAll());
	}

}
