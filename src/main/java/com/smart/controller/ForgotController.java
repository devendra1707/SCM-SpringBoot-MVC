package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.model.User;
import com.smart.repository.UserRepository;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository repository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Email ID form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}

	Random random = new Random(1000);

	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {
		System.out.println("Email :: " + email);
		// Generating OTP of 4 digit

		int otp = random.nextInt(9999);
		System.out.println("OTP :: " + otp);
		// write code for send OTP

		String subject = "OTP From SCM";
		String message = "" + "<div style='border:1px solid #e2e2e2; padding:20px'>" + "<h1>" + "OTP is :: " + "<b>"
				+ otp + "</b>" + "</h1>" + "</div>";
		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {
			session.setAttribute("myOtp", otp);
			session.setAttribute("email", email);
			return "verify-otp";

		} else {
			session.setAttribute("message", "Check your email id !!");

			return "forgot_email_form";
		}

	}

	// verify OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		int myOtp = (int) session.getAttribute("myOtp");
		String email = (String) session.getAttribute("email");

		if (myOtp == otp) {
			// password change form

			User user = repository.getUserByUserName(email);

			if (user == null) {
				// send error message
				session.setAttribute("message", "User don't exist !!");

				return "forgot_email_form";
			} else {
				// send password

			}

			return "password_change_form";
		} else {
			session.setAttribute("message", "You have entered wrong otp !!");
			return "verify-otp";
		}

	}

// Change Password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {

		String email = (String) session.getAttribute("email");
		User user = this.repository.getUserByUserName(email);
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		repository.save(user);
		return "redirect:/signin?change=password changed successfully...";
	}
}
