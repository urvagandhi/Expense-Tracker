package com.tracker.expense_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.expense_tracker.entity.Budget;
import com.tracker.expense_tracker.service.BudgetService;


@RestController
@RequestMapping("/api/budgets")

public class BudgetController {

	
	@Autowired
	private BudgetService budgetService;
	
	@PostMapping("/add-budget")
	public String addBudget(@RequestBody Budget budget) {
		budgetService.addBudget(budget);
		return "budget data added successsfully";
	}
	
	@PutMapping("/update-budget/{id}")
	public String  updateBudget(@PathVariable Long id,@RequestBody Budget budget) {
		budgetService.updateBudget(id,budget);
		return "budget update successfully";
	}
	
	@DeleteMapping("/delete-budget")
	public String deleteBudget(@RequestBody Budget budget) {
		budgetService.deleteBudget(budget.getId());
		return "budget data deleted successfully";
	}
	
	@GetMapping("/budget-data/{userId}/{month}/{year}")
	public Budget getBudget(@PathVariable Long userId ,@PathVariable int month , @PathVariable int year) {
		return budgetService.getBudget(userId , month , year);
	}

}
