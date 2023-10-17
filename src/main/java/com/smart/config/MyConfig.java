package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MyConfig {

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(getUserDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// configuration

//		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable())
//				.authorizeHttpRequests(
//						auth -> auth.requestMatchers("/admin/**")
//						.hasRole("ADMIN").requestMatchers("/user/**")
//								.hasRole("USER").requestMatchers("/**")
//								.permitAll().anyRequest().authenticated())
//				.formLogin().loginPage("/signin")
//				.loginProcessingUrl("/dologin")
//				.defaultSuccessUrl("/user/index")
//				.failureUrl("/login-fail")
//				.and().csrf().disable();
//		return http.build();
		
		http.csrf().disable()
		.authorizeHttpRequests()
		
//		.requestMatchers("/admin/**").hasRole("ADMIN")
//		
//		.requestMatchers("/user/**").hasRole("USER")
		
		.requestMatchers("/**").permitAll()
		
		.anyRequest()
		.authenticated()
		.and()
		.formLogin().loginPage("/signin")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index");
//		.failureUrl("/login-fail");
		return http.build();
	}

}

