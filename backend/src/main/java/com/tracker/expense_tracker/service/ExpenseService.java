package com.tracker.expense_tracker.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tracker.expense_tracker.dto.request.ExpenseRequest;
import com.tracker.expense_tracker.dto.response.ExpenseResponse;
import com.tracker.expense_tracker.entity.Expense;
import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.exception.ResourceNotFoundException;
import com.tracker.expense_tracker.repository.BudgetRepository;
import com.tracker.expense_tracker.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    @Transactional
    public ExpenseResponse addExpense(User user, ExpenseRequest request) {
        Expense expense = Expense.builder()
                .user(user)
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .build();

        Expense saved = expenseRepository.save(expense);
        updateBudgetTotalExpense(user.getId(), request.getExpenseDate().getMonthValue(), request.getExpenseDate().getYear());
        log.info("Expense added: {} for user {}", saved.getId(), user.getEmail());
        return ExpenseResponse.from(saved);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long id, User user, ExpenseRequest request) {
        Expense expense = findExpenseForUser(id, user);

        int oldMonth = expense.getExpenseDate().getMonthValue();
        int oldYear = expense.getExpenseDate().getYear();

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());

        Expense updated = expenseRepository.save(expense);

        // Recalculate budget totals for both old and new months
        updateBudgetTotalExpense(user.getId(), oldMonth, oldYear);
        updateBudgetTotalExpense(user.getId(), request.getExpenseDate().getMonthValue(), request.getExpenseDate().getYear());

        log.info("Expense updated: {}", updated.getId());
        return ExpenseResponse.from(updated);
    }

    @Transactional
    public void deleteExpense(Long id, User user) {
        Expense expense = findExpenseForUser(id, user);
        int month = expense.getExpenseDate().getMonthValue();
        int year = expense.getExpenseDate().getYear();

        expenseRepository.delete(expense);
        updateBudgetTotalExpense(user.getId(), month, year);
        log.info("Expense deleted: {}", id);
    }

    public Page<ExpenseResponse> getExpensesByUser(User user, Pageable pageable) {
        return expenseRepository.findByUserId(user.getId(), pageable)
                .map(ExpenseResponse::from);
    }

    public List<ExpenseResponse> getAllExpensesByUser(User user) {
        return expenseRepository.findByUserId(user.getId()).stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    private Expense findExpenseForUser(Long expenseId, User user) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Expense", "id", expenseId);
        }
        return expense;
    }

    private void updateBudgetTotalExpense(Long userId, int month, int year) {
        budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .ifPresent(budget -> {
                    Double total = expenseRepository.sumByUserIdAndMonthAndYear(userId, month, year);
                    budget.setTotalExpense(total != null ? total : 0.0);
                    budgetRepository.save(budget);
                });
    }
}
