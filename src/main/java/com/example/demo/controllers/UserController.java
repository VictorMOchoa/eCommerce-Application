package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		logger.info("Received request to retrieve user with userId " + id);
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		logger.info("Received request to retrieve user " + username);
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		logger.info("Received request to create new user.");
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		if (!passwordsMatch(createUserRequest)) {
			logger.error("Password and password confirmation did not match.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (!passwordLengthIsValid(createUserRequest)) {
			logger.error("Provided password did not meet length criteria.");
			return ResponseEntity.badRequest().build();
		}

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		userRepository.save(user);
		logger.info("User " + user.getUsername() + " was successfully created.");
		return ResponseEntity.ok(user);
	}

	private boolean passwordLengthIsValid(CreateUserRequest createUserRequest) {
		return createUserRequest.getPassword().length() >= 5;
	}

	private boolean passwordsMatch(CreateUserRequest createUserRequest) {
		return createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword());
	}

}
