package com.example.demo.controllers;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			logger.error("Exception: User not found");
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {

		//User already exist Exception
		if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
			logger.error("Exception: User already exist");
			return ResponseEntity.notFound().build();
		}
		// Check if the password is at least 7 characters long
		if(createUserRequest.getPassword().length() < 7){
			logger.error("CREATE_USER_FAILURE: Password less than 7 characters.");
			return ResponseEntity.badRequest().build();
		}

		// Check if the password matches the confirmation password
		if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			logger.error("CREATE_USER_FAILURE: Password and confirmation password do not match.");
			return ResponseEntity.badRequest().build();
		}

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);



		/*
		// generate a random salt
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		String encodedSalt = Base64.getEncoder().encodeToString(salt);

		// append salt to password and hash it
		String saltedPassword = createUserRequest.getPassword() + encodedSalt;
		user.setPassword(bCryptPasswordEncoder.encode(saltedPassword));
		user.setSalt(encodedSalt);
		*/
		String password = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
		user.setPassword(password);
		userRepository.save(user);
		logger.info("CREATE_USER_SUCCESS : " + createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}
}
