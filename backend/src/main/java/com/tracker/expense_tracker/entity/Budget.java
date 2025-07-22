package com.tracker.expense_tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "budget")
public class Budget {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id" , nullable = false)
	private User user;
	
	@Column(nullable = false)
	private int month;
	
	@Column(nullable = false)
	private int year;
	
	@Column(nullable = false)
	private Double budgetLimit;
	
	@Column(nullable = false)
	private Double totalExpense = 0.0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
//
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Double getBudgetLimit() {
		return budgetLimit;
	}

	public void setBudgetLimit(Double budgetLimit) {
		this.budgetLimit = budgetLimit;
	}

	public Double getTotalExpense() {
		return totalExpense;
	}

	public void setTotalExpense(Double totalExpense) {
		this.totalExpense = totalExpense;
	}
	
	

}
