package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.service.ClinicalNoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ClinicalNoteController {
	
	private final ClinicalNoteRepository clinicalNoteRepository;
	
	public ClinicalNoteController(ClinicalNoteRepository _clinicalNoteRepository) {
		this.clinicalNoteRepository = _clinicalNoteRepository;
	}
	
	@GetMapping("/user/show_clinical_notes")
	public ModelAndView showAllClinicalNotes() {
		return new ModelAndView("/user/clinical_note_table", "cnotes", clinicalNoteRepository.findAll());
	}

}
