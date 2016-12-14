package org.cardiacatlas.xpacs.web;

import org.cardiacatlas.xpacs.service.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
	
	private final UserRepository userRepository;
	
	public UserController(UserRepository _userRepository) {
		this.userRepository = _userRepository;
	}
	
	@GetMapping("/admin/show_user")
	public ModelAndView patientHistoryAll() {
		return new ModelAndView("/admin/user_table", "users", userRepository.findAll());
	}
}
