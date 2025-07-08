package com.tracker.expense_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracker.expense_tracker.entity.Expense;
import com.tracker.expense_tracker.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

	
	
	

}
