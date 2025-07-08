package com.tracker.expense_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.repository.UserRepository;

@Service
public class UserService {
	
	
	@Autowired
	private UserRepository userRepository;
	
	
	public  User registerUser(User user) {
		return userRepository.save(user);
		
	}
	
	public List<User> getUser(Long id){
		return  userRepository.findAll();
		 
	}

	public  User updateUser(Long id , User user) {
		User existingUser = userRepository.findById(id).orElseThrow();
		existingUser.setUsername(user.getUsername());
		existingUser.setEmail(user.getEmail());
		existingUser.setPassword(user.getPassword());
		return  userRepository.save(existingUser);		
	}

	public void  deleteUser(Long id) {
		userRepository.deleteById(id);		
	}

	
	
}
