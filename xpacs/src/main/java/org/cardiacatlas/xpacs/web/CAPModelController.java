package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.service.CAPModelRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CAPModelController {

	private final CAPModelRepository capModelRepository;
	
	public CAPModelController(CAPModelRepository _capModelRepository) {
		this.capModelRepository = _capModelRepository;
	}
	
	@GetMapping("/user/show_cap_models")
	public ModelAndView showAllCAPModels() {
		return new ModelAndView("/user/cap_model_table", "models", this.capModelRepository.findAll());
	}
	
}
