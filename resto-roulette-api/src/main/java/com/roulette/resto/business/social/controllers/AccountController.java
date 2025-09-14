package com.roulette.resto.business.social.controllers;

import com.roulette.resto.business.social.dto.AuthResponse;
import com.roulette.resto.business.social.dto.LoginDto;
import com.roulette.resto.business.social.dto.RegisterDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.repository.AccountRepository;
import com.roulette.resto.common.configuration.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
public class AccountController {
	private final AuthenticationManager authenticationManager;

	private final PasswordEncoder passwordEncoder;

	private final AccountRepository accountRepository;

	private final JwtService jwtService;
	public AccountController(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, AccountRepository accountRepository, JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.accountRepository = accountRepository;
		this.jwtService = jwtService;
	}
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto, HttpServletRequest request){
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword());
		Authentication auth = authenticationManager.authenticate(authReq);
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(auth);
		HttpSession session = request.getSession();
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
		AuthResponse authResponse = new AuthResponse();
		String jwtToken = jwtService.generateToken(accountRepository.getAccountByLogin(loginDto.getLogin()));
		authResponse.setToken(jwtToken);
		authResponse.setExpiresIn(jwtService.getExpirationTime());
		return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}


	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto, HttpServletRequest request){

		// add check for email exists in DB
//		if(userRepository.existsByEmail(signupRequest.getEmail())){
//			return new ResponseEntity<>("Email already used!", HttpStatus.BAD_REQUEST);
//		}


		// create account object
		Account account = new Account();
		log.warn(account.toString());
		account.setLogin(registerDto.getLogin());
		account.setPassword(registerDto.getPassword());
		account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		log.warn(account.toString());

//		Role role = roleRepository.findByName("ROLE_USER");
//		account.setRoles(Set.of(role));

		accountRepository.registerAccount(account);

		return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request){
		System.out.println("logging out");
		request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
		request.getSession().invalidate();
		SecurityContextHolder.clearContext();
		return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);


	}
}
