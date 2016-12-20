package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.service.AuxFileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuxFileController {

	private final AuxFileRepository auxFileRepository;
	
	public AuxFileController(AuxFileRepository _auxFileRepository) {
		this.auxFileRepository = _auxFileRepository;
	}
	
	@GetMapping("/user/show_aux_files")
	public ModelAndView viewAllAuxFile() {
		return new ModelAndView("/user/aux_file_table", "files", this.auxFileRepository.findAll());
	}
	
}
