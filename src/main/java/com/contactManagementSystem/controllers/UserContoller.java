package com.contactManagementSystem.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contactManagementSystem.dao.UserRepository;
import com.contactManagementSystem.entities.User;

@Controller
@RequestMapping("/user")
public class UserContoller {
	
	@Autowired
	private UserRepository userRepository;
	
	
	@GetMapping("/index")
	public String dashboard(Model model,Principal principal) {
		String username = principal.getName();
		
		User user = this.userRepository.getUserByUserName(username);
		
		model.addAttribute("user",user);
		
		
		return "normal/user_dashboard";
	}
	

}
