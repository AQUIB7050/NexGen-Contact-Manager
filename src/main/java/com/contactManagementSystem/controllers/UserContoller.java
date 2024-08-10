package com.contactManagementSystem.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactManagementSystem.dao.ContactRepository;
import com.contactManagementSystem.dao.UserRepository;
import com.contactManagementSystem.entities.Contact;
import com.contactManagementSystem.entities.User;
import com.contactManagementSystem.helper.Message;

@Controller
@RequestMapping("/user")
public class UserContoller {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// It will run for all the methods.
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String username = principal.getName();

		User user = this.userRepository.getUserByUserName(username);

		model.addAttribute("user", user);

	}

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "user - dashboard");

		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "add contact - Smart Contact Manager");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact";
	}

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, Model model) {

		try {
			String email = principal.getName();
			User user = this.userRepository.getUserByUserName(email);

			// processing and updating file
			if (file.isEmpty()) {

				System.out.println("File is empty");

			} else {
				// upload the file to folder, then update the name in contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);

			model.addAttribute("message", new Message("contact has been successfully added", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", new Message("something went wrong! try again", "danger"));
		}

		return "normal/user_dashboard";
	}

	@GetMapping("/show-contacts/{page}")
	public String showContact(Model model, Principal principal, @PathVariable("page") Integer page) {
		model.addAttribute("title", "contacts - Smart Contact Manager");
		
		try {
			String email = principal.getName();
			User user = this.userRepository.getUserByUserName(email);
			
			PageRequest pageable = PageRequest.of(page, 1);
			
			Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
			
			model.addAttribute("contact",contacts);
			model.addAttribute("currentPage",page);
			model.addAttribute("totalPages", contacts.getTotalPages());
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		
		return "normal/show_contacts";
	}

}
