package com.roulette.resto.business.social.controllers;

import com.roulette.resto.business.social.dto.AuthResponse;
import com.roulette.resto.business.social.dto.LoginDto;
import com.roulette.resto.business.social.dto.RegisterDto;
import com.roulette.resto.business.social.entity.Account;
import com.roulette.resto.business.social.repository.AccountRepository;
import com.roulette.resto.business.social.services.AccountService;
import com.roulette.resto.common.configuration.JwtService;
import com.roulette.resto.common.exception.APIError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.security.auth.login.AccountNotFoundException;

@Slf4j
@Controller
public class AccountController {
	private final AuthenticationManager authenticationManager;

	private final PasswordEncoder passwordEncoder;

	private final AccountRepository accountRepository;

	private final JwtService jwtService;
	private final AccountService accountService;

	public AccountController(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, AccountRepository accountRepository, JwtService jwtService, AccountService accountService) {
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.accountRepository = accountRepository;
		this.jwtService = jwtService;
		this.accountService = accountService;
	}
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletRequest request){
		try {
			UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword());
			Authentication auth = authenticationManager.authenticate(authReq);
			SecurityContext sc = SecurityContextHolder.getContext();
			sc.setAuthentication(auth);
			HttpSession session = request.getSession();
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());


			AuthResponse authResponse = new AuthResponse();
			String jwtToken = jwtService.generateToken(accountService.loadUserByUsername(loginDto.getLogin()));
			authResponse.setToken(jwtToken);
			authResponse.setExpiresIn(jwtService.getExpirationTime());
			return new ResponseEntity<>(authResponse, HttpStatus.OK);

		}catch (AuthenticationException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(new APIError("Login failed", e.getMessage()), HttpStatus.UNAUTHORIZED);
		}
	}


	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto, HttpServletRequest request){
		if(accountService.existsByLogin(registerDto.getLogin())){
			return new ResponseEntity<>(new APIError("Login already exist"), HttpStatus.BAD_REQUEST);
		}
		if(accountService.existsByEmail(registerDto.getEmail())){
			return new ResponseEntity<>(new APIError("Email already exist"), HttpStatus.BAD_REQUEST);
		}
		Account account = new Account();
		log.warn(account.toString());
		account.setLogin(registerDto.getLogin());
		account.setPassword(registerDto.getPassword());
		account.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		try {
			accountRepository.registerAccount(account);
		}catch (Exception e) {
			return new ResponseEntity<>((new APIError("DB error while creating account", e.getMessage())),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		AuthResponse authResponse = new AuthResponse();
		String jwtToken = jwtService.generateToken(accountRepository.getAccountByLogin(registerDto.getLogin()));
		authResponse.setToken(jwtToken);
		authResponse.setExpiresIn(jwtService.getExpirationTime());
		return new ResponseEntity<>(authResponse, HttpStatus.OK);

	}
}
