package com.contactManagementSystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserContoller {
	
	
	@GetMapping("/index")
	public String dashboard() {
		return "normal/user_dashboard";
	}

}
