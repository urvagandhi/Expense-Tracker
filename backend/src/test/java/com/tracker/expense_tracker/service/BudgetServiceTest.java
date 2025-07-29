package com.tracker.expense_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tracker.expense_tracker.dto.request.BudgetRequest;
import com.tracker.expense_tracker.dto.response.BudgetResponse;
import com.tracker.expense_tracker.entity.Budget;
import com.tracker.expense_tracker.entity.Role;
import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.exception.DuplicateResourceException;
import com.tracker.expense_tracker.exception.ResourceNotFoundException;
import com.tracker.expense_tracker.repository.BudgetRepository;
import com.tracker.expense_tracker.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private ExpenseRepository expenseRepository;

    @InjectMocks
    private BudgetService budgetService;

    private User user;
    private BudgetRequest budgetRequest;
    private Budget budget;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").username("Test").role(Role.USER).build();

        budgetRequest = BudgetRequest.builder()
                .month(12)
                .year(2025)
                .budgetLimit(5000.0)
                .build();

        budget = Budget.builder()
                .id(1L)
                .user(user)
                .month(12)
                .year(2025)
                .budgetLimit(5000.0)
                .totalExpense(1200.0)
                .build();
    }

    @Test
    @DisplayName("Should create budget successfully")
    void addBudget_Success() {
        when(budgetRepository.existsByUserIdAndMonthAndYear(1L, 12, 2025)).thenReturn(false);
        when(expenseRepository.sumByUserIdAndMonthAndYear(1L, 12, 2025)).thenReturn(1200.0);
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        BudgetResponse response = budgetService.addBudget(user, budgetRequest);

        assertThat(response.getBudgetLimit()).isEqualTo(5000.0);
        assertThat(response.getRemainingBudget()).isEqualTo(3800.0);
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException for duplicate month/year")
    void addBudget_Duplicate() {
        when(budgetRepository.existsByUserIdAndMonthAndYear(1L, 12, 2025)).thenReturn(true);

        assertThatThrownBy(() -> budgetService.addBudget(user, budgetRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get budget by month/year")
    void getBudget_Success() {
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 12, 2025)).thenReturn(Optional.of(budget));

        BudgetResponse response = budgetService.getBudget(user, 12, 2025);

        assertThat(response.getMonth()).isEqualTo(12);
        assertThat(response.getYear()).isEqualTo(2025);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent budget")
    void getBudget_NotFound() {
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 1, 2026)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> budgetService.getBudget(user, 1, 2026))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
