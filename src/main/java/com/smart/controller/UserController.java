package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.healper.Message;
import com.smart.model.Contact;
import com.smart.model.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")

public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String userName = p.getName();
		System.out.println("UserName" + userName);
		// get the user using username(email)
		User user = userRepository.getUserByUserName(userName);

		System.out.println("User :: " + user);

		m.addAttribute("user", user);
	}

	// dashboard Home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
//		String userName = principal.getName();
//		System.out.println("UserName" + userName);
//		// get the user using username(email)
//		User user = userRepository.getUserByUsername(userName);
//
//		System.out.println("User :: " + user);
//
//		model.addAttribute("user", user);
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing
//	@PostMapping("/process-contact")
//	public String processContact(@ModelAttribute("contact") Contact contact, @Param("profileImage") MultipartFile file,
//			Principal principal, HttpSession session) {
//
//		try {
//			String name = principal.getName();
//			User user = this.userRepository.getUserByUserName(name);
//
//			// processing and uploading file ...
//
//			if (file.isEmpty()) {
////			 if the file is empty than try our message
//			System.out.println("File is empty");
//			contact.setImage("default.png");
//			} else {
////			 file the file to folder and update the name to contact
//				contact.setImage(file.getOriginalFilename());
//
//				contact.setImage(file.getOriginalFilename());
//				File saveFile = new ClassPathResource("static/img").getFile();
//
//				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
//
//				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//			System.out.println("Image is Uploaded");
//			}
//
//			user.getContacts().add(contact);
//
//			contact.setUser(user);
//			this.userRepository.save(user);
//
//			System.out.println("Data :: " + contact);
//			System.out.println("Added to database");
//
//			// message success
//			session.setAttribute("message", new Message("Your contact is added !! Add more", "success"));
//
//		} catch (Exception e) {
//			System.out.println("ERROR :: " + e.getMessage());
//			e.printStackTrace();
//
//			// message error
//			session.setAttribute("message", new Message("Something went wrong !! Try again", "danger"));
//
//		}
//		return "normal/add_contact_form";
//	}

	
	///////////////////////////////////////////////////////
	
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// Processing and uploading file
			if (file.isEmpty()) {
				// if file is empty then try your message
				System.out.println("file is empty");
				// if contact file img file is empty we pass this msg
				contact.setImage("contact.jpg");
			} else {

				// file the file to folder and update the conctact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is Uploaded");

			}
			contact.setUser(user);

			user.getContacts().add(contact);
			this.userRepository.save(user);

			// print contact data from user
			System.out.println("DATA " + contact);
			System.out.println("Added To Database");
			// message success ......... to database
			session.setAttribute("message", new Message("Your Contact is added !! ADD more ....", "success"));
		} catch (Exception e) {

			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			/// error message to send
			session.setAttribute("message", new Message("Something Went wrong !!", "ISSUE"));
		}
		return "normal/add_contact_form";
	}
	
	
	///////////////////////////////////////////////////////////////////////
// Show Contacts handler
	// per page = 5(n)
	// current page = 0 [page]

	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contact");

		// List of contacts
		String userName = principal.getName();

		User user = this.userRepository.getUserByUserName(userName);

		// Current Page
		// Contact per page
		Pageable pageable = PageRequest.of(page, 5);

//		List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId());
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);

		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show-contacts";
	}

	// Showing particular contact detao;s
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		System.out.println("CID :: " + cId);
		return "normal/contact_detail";
	}

	// Contact handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, HttpSession session,
			Principal principal) {

		System.out.println("CID :: " + cId);
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

// checking...

		System.out.println("Contact :: " + contact.getcId());
//		contact.setUser(null);

		User user = this.userRepository.getUserByUserName(principal.getName());

		user.getContacts().remove(contact);

//		this.contactRepository.delete(contact);

		this.userRepository.save(user);

		System.out.println("DELETED");
		session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));

		return "redirect:/user/show-contacts/0";
	}

	// Update Contact Handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId, Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal principal) {
		// check if person hasa selected new img we gonna rewrite or is it empty
		try {
			// old photo del fetch
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();
			if (!file.isEmpty()) {
				// file work
				// working on img by del old img and bringing new latest one
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldcontactDetail.getImage());
				file1.delete();
				// update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldcontactDetail.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("messaage", new Message("Your Contact had been updated .....", "Success"));

		} catch (Exception e) {
			e.setStackTrace(null);
			// TODO: handle exception
		}
		System.out.println("Contact Name " + contact.getName());
		System.out.println("Contact ID" + contact.getcId());

		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// Your Profile Handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {

		model.addAttribute("title", "Profile PAge");
		return "normal/profile";
	}

	// Open Setting Handler
	@GetMapping("/settings")
	public String openSetting() {

		return "normal/settings";
	}

	// Change Password
	@PostMapping("/change-password")
	public String chagePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		System.out.println("Old Password :: " + oldPassword + "\nNew Password :: " + newPassword);
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());

		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			// change the password
			currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));

			this.userRepository.save(currentUser);

			session.setAttribute("message", new Message("Your password is successfully changed...", "success"));

		} else {
//			error....
			session.setAttribute("message", new Message("Please Enter Your Correct Password...", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/index";
	}

}
