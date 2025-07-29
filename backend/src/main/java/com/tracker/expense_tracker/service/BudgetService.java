package com.tracker.expense_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tracker.expense_tracker.dto.request.BudgetRequest;
import com.tracker.expense_tracker.dto.response.BudgetResponse;
import com.tracker.expense_tracker.entity.Budget;
import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.exception.DuplicateResourceException;
import com.tracker.expense_tracker.exception.ResourceNotFoundException;
import com.tracker.expense_tracker.repository.BudgetRepository;
import com.tracker.expense_tracker.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetResponse addBudget(User user, BudgetRequest request) {
        if (budgetRepository.existsByUserIdAndMonthAndYear(user.getId(), request.getMonth(), request.getYear())) {
            throw new DuplicateResourceException("Budget", "month/year",
                    request.getMonth() + "/" + request.getYear());
        }

        Double currentExpense = expenseRepository.sumByUserIdAndMonthAndYear(
                user.getId(), request.getMonth(), request.getYear());

        Budget budget = Budget.builder()
                .user(user)
                .month(request.getMonth())
                .year(request.getYear())
                .budgetLimit(request.getBudgetLimit())
                .totalExpense(currentExpense != null ? currentExpense : 0.0)
                .build();

        Budget saved = budgetRepository.save(budget);
        log.info("Budget created for {}/{} by user {}", request.getMonth(), request.getYear(), user.getEmail());
        return BudgetResponse.from(saved);
    }

    public BudgetResponse updateBudget(Long id, User user, BudgetRequest request) {
        Budget budget = findBudgetForUser(id, user);
        budget.setBudgetLimit(request.getBudgetLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget updated = budgetRepository.save(budget);
        log.info("Budget updated: {}", updated.getId());
        return BudgetResponse.from(updated);
    }

    public void deleteBudget(Long id, User user) {
        Budget budget = findBudgetForUser(id, user);
        budgetRepository.delete(budget);
        log.info("Budget deleted: {}", id);
    }

    public BudgetResponse getBudget(User user, int month, int year) {
        Budget budget = budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Budget not found for %d/%d", month, year)));
        return BudgetResponse.from(budget);
    }

    public List<BudgetResponse> getAllBudgets(User user) {
        return budgetRepository.findByUserId(user.getId()).stream()
                .map(BudgetResponse::from)
                .toList();
    }

    private Budget findBudgetForUser(Long budgetId, User user) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Budget", "id", budgetId);
        }
        return budget;
    }
}
