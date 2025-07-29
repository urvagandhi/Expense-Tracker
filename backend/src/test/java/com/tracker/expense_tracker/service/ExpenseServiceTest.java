package com.tracker.expense_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tracker.expense_tracker.dto.request.ExpenseRequest;
import com.tracker.expense_tracker.dto.response.ExpenseResponse;
import com.tracker.expense_tracker.entity.Expense;
import com.tracker.expense_tracker.entity.Role;
import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.exception.ResourceNotFoundException;
import com.tracker.expense_tracker.repository.BudgetRepository;
import com.tracker.expense_tracker.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private BudgetRepository budgetRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User user;
    private ExpenseRequest expenseRequest;
    private Expense expense;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").username("Test").role(Role.USER).build();

        expenseRequest = ExpenseRequest.builder()
                .amount(100.0)
                .category("Food")
                .description("Lunch")
                .expenseDate(LocalDate.of(2025, 12, 15))
                .build();

        expense = Expense.builder()
                .id(1L)
                .user(user)
                .amount(100.0)
                .category("Food")
                .description("Lunch")
                .expenseDate(LocalDate.of(2025, 12, 15))
                .build();
    }

    @Test
    @DisplayName("Should add expense successfully")
    void addExpense_Success() {
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 12, 2025)).thenReturn(Optional.empty());

        ExpenseResponse response = expenseService.addExpense(user, expenseRequest);

        assertThat(response.getAmount()).isEqualTo(100.0);
        assertThat(response.getCategory()).isEqualTo("Food");
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should get all expenses for a user")
    void getAllExpenses_Success() {
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of(expense));

        List<ExpenseResponse> result = expenseService.getAllExpensesByUser(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Food");
    }

    @Test
    @DisplayName("Should delete expense successfully")
    void deleteExpense_Success() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 12, 2025)).thenReturn(Optional.empty());

        expenseService.deleteExpense(1L, user);

        verify(expenseRepository).delete(expense);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent expense")
    void deleteExpense_NotFound() {
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.deleteExpense(99L, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when expense belongs to another user")
    void deleteExpense_WrongUser() {
        User otherUser = User.builder().id(2L).email("other@example.com").build();
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> expenseService.deleteExpense(1L, otherUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
