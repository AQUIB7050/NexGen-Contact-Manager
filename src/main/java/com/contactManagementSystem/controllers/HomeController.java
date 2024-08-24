package com.contactManagementSystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactManagementSystem.dao.UserRepository;
import com.contactManagementSystem.entities.User;
import com.contactManagementSystem.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute User user, BindingResult result, @RequestParam(value="agreement" ,defaultValue="false") boolean agreement, Model model, HttpSession session) {
		
		try {
			
			if(!agreement) {
				throw new Exception("You have not agreed term and conditions");
			}
			
			if(result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default_user.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Succesfully Registered!","alert-success"));
			
			userRepository.save(user);
			
			System.out.println(user);
			
		} catch (Exception e) {
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong! " + e.getMessage(),"alert-danger"));
			e.printStackTrace();
			
		}
		
		if(session.getAttribute("message") != null) {
			model.addAttribute("message", session.getAttribute("message"));
			session.removeAttribute("message");
		}
		
		
		return "signup";
		
		
		
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "login - Smart Contact Manager");
		return "login";
	}
	
	@GetMapping("/login_fail")
	public String loginFail(Model model) {
		model.addAttribute("title", "login - fail");
		return "login_fail";
	}

}
