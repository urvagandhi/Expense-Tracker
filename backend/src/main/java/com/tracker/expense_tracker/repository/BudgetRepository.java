package com.tracker.expense_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracker.expense_tracker.entity.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget , Long>{

	
	public Budget findByUserIdAndMonthAndYear(Long  userId, int month , int year );
	
	
}
