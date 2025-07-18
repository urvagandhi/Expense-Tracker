package com.tracker.expense_tracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.expense_tracker.entity.Expense;
import com.tracker.expense_tracker.repository.ExpenseRepository;
import com.tracker.expense_tracker.service.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
	
	@Autowired
	private ExpenseService expenseService;
	
	@PostMapping("/add-expense")
	public String  addExpense(@RequestBody Expense expense) {
		 expenseService.addExpense(expense);
		 return "expense added successfully";
		
	}
	
	@PutMapping("/update-expense/{id}")
	public String  updateExpense(@PathVariable Long id,@RequestBody Expense expense) {
		expenseService.updateExpense(id , expense);
		return "expense data update successfully";
		
	}
	
	@DeleteMapping("/delete-expense")
	public String deleteExpense(@RequestBody Expense expense) {
		expenseService.deleteExpense(expense.getId());
		return "expense data deleted succesfully";
		
	}
	
	
	
	@GetMapping("/expenses/{userId}")
	public List<Expense> getExpenseByUserId(@PathVariable Long userId){
		return expenseService.getExpenseByUserId(userId);
	}
	

}
