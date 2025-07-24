package com.tracker.expense_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.expense_tracker.entity.Budget;
import com.tracker.expense_tracker.repository.BudgetRepository;

@Service
public class BudgetService {

	
	@Autowired
	private BudgetRepository  budgetRepository;
	
	
	public void  addBudget(Budget budget){
		budgetRepository.save(budget);
	
	
	}
	public Budget updateBudget(Long id , Budget budget) {
		Budget existingBudget = budgetRepository.findById(id).orElseThrow();
		existingBudget.setBudgetLimit(budget.getBudgetLimit());
		existingBudget.setMonth(budget.getMonth());
		existingBudget.setYear(budget.getYear());
		existingBudget.setTotalExpense(budget.getTotalExpense());

		return budgetRepository.save(existingBudget);
	}
	public void deleteBudget(Long id) {
		budgetRepository.deleteById(id);
	}
	
	public Budget getBudget(Long  userId,int month , int year) {
		return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
		
		
	}
}
