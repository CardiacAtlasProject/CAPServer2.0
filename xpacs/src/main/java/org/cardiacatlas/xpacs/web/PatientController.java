package org.cardiacatlas.xpacs.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

	@RequestMapping("/view/patlist")
	public String getPatientList() {
		return "Patient lists";
	}
	
}
