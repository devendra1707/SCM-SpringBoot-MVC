package com.smart.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.healper.Message;
import com.smart.model.User;
import com.smart.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
//@EnableMethodSecurity
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository repository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model model) {

		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

//	@PreAuthorize("hasRole('USER')")
	@GetMapping("/about")
	public String about(Model model) {

		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@RequestMapping(value = "/signup")
	public String SignUp(Model model) {
		model.addAttribute("title", "SignUp-Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// handler Method
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,
			@RequestParam(value = "agreement", defaultValue = "false") Boolean agreement, BindingResult result,
			Model model, HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}

			if (result.hasErrors()) {
				System.out.println("ERROR" + result.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agree :: " + agreement);
			System.out.println("User :: " + user);

			this.repository.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered !! ", "alert-success"));
			return "redirect:/signin";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	// handle for custom login
	@GetMapping("/signin")
	public String CustomLogin(Model model) {

		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
}
