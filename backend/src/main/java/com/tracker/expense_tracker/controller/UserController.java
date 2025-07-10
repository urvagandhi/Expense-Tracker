package com.tracker.expense_tracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.service.UserService;

@RestController
@RequestMapping("/api-users")
public class UserController {

	
	@Autowired
	private UserService userService;
	
	@PostMapping("/register")
	public String registerUser(@RequestBody User user) {
		userService.registerUser(user);
		return "user registered successfully";
		
	}
	
	@GetMapping("/get-user-data")
	public List<User> getuserById(@PathVariable  Long id ){
		return userService.getUser(id);
	}
	
	@PutMapping("/update-user/{id}")
	public String updateUser(@PathVariable  Long id , @RequestBody User user) {
		userService.updateUser(id , user);
		return "user data updated successfully";
	}
	
	@DeleteMapping("/delete-user/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "user data deleted successfully";
	
	}
	
}
