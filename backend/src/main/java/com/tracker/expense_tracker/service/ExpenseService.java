package com.tracker.expense_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.expense_tracker.entity.Expense;
import com.tracker.expense_tracker.repository.ExpenseRepository;

@Service
public class ExpenseService {
	
	@Autowired
	private ExpenseRepository expenseRepository;
	
	
	public void addExpense( Expense expense) {
		expenseRepository.save(expense);
	}
	
	
	public Expense updateExpense(Long id ,Expense expense) {
		Expense existingExpense =  expenseRepository.findById(id).orElseThrow();
		existingExpense.setDescription(expense.getDescription());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setExpenseDate(expense.getExpenseDate());
		return expenseRepository.save(existingExpense);
	}
	
	public void  deleteExpense(Long id ) {		
		expenseRepository.deleteById(id);;
	}
	
	
	public List<Expense> getExpenseByUserId(Long userId){
		return expenseRepository.findByUserId(userId);
	}


	


	
	


}
