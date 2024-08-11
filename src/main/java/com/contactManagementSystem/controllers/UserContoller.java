package com.contactManagementSystem.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
				contact.setImage("default_contact.png");

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

			PageRequest pageable = PageRequest.of(page, 3);

			Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

			model.addAttribute("contact", contacts);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", contacts.getTotalPages());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "normal/show_contacts";
	}

	@GetMapping("/{contactId}/contact")
	public String getContactDetails(@PathVariable("contactId") int contactId, Model model, Principal principal) {

		Optional<Contact> optionalContact = this.contactRepository.findById(contactId);
		Contact contact = optionalContact.get();

		// FOR SECURITY PURPOSE, SO ONE CANNOT CEHCK ANOTHERS CONTACT
		String email = principal.getName();
		User user = this.userRepository.getUserByUserName(email);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName() + " - Smart Contact Manager");
		}

		return "normal/contact_detail";

	}

	@GetMapping("/delete/{contactId}/{currentPage}")
	public String deleteContact(@PathVariable("contactId") int contactId, @PathVariable("currentPage") int currentPage,
			Principal principal, Model model) {
		Optional<Contact> optionalContact = this.contactRepository.findById(contactId);
		Contact contact = optionalContact.get();
		User user = this.userRepository.getUserByUserName(principal.getName());

		// FOR SECURITY, SO ANTOHER PERSON CANNOT DELETE OTHER CONTACT
		if (user.getId() == contact.getUser().getId()) {
			// UNLINK USER SO IT CAN BE DELETED
			contact.setUser(null);
			// REMOVE IMAGE (img/contact.getImage())
			if(!contact.getImage().equals("default_contact.png")) {
				try {
					File savedFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + contact.getImage());
					Files.delete(path);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("File Not Found");
				}
			}
			

			this.contactRepository.delete(contact);
			model.addAttribute("message", new Message("Contact Deleted Successfully", "success"));
		}

		// FOR HANDLING DELETE BUTTON OF CONTACT_DETAIL PAGE
		if (currentPage == -1) {
			return ("redirect:/user/show-contacts/0");
		}

		return ("redirect:/user/show-contacts/" + currentPage);

	}

}
