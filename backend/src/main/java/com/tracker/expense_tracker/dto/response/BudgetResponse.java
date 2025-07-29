package com.tracker.expense_tracker.dto.response;

import java.time.LocalDateTime;

import com.tracker.expense_tracker.entity.Budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {

    private Long id;
    private int month;
    private int year;
    private Double budgetLimit;
    private Double totalExpense;
    private Double remainingBudget;
    private LocalDateTime createdAt;

    public static BudgetResponse from(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .month(budget.getMonth())
                .year(budget.getYear())
                .budgetLimit(budget.getBudgetLimit())
                .totalExpense(budget.getTotalExpense())
                .remainingBudget(budget.getBudgetLimit() - budget.getTotalExpense())
                .createdAt(budget.getCreatedAt())
                .build();
    }
}
