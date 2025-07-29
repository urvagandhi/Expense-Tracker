package com.tracker.expense_tracker.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tracker.expense_tracker.entity.Expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;
    private Double amount;
    private String category;
    private String description;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;

    public static ExpenseResponse from(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
