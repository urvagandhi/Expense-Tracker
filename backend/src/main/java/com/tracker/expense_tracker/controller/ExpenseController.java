package com.tracker.expense_tracker.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tracker.expense_tracker.dto.request.ExpenseRequest;
import com.tracker.expense_tracker.dto.response.ApiResponse;
import com.tracker.expense_tracker.dto.response.ExpenseResponse;
import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.service.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense management endpoints")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @Operation(summary = "Add a new expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> addExpense(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.addExpense(user, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense added successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.updateExpense(id, user, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        expenseService.deleteExpense(id, user);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all expenses for current user (paginated)")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpenses(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ExpenseResponse> response = expenseService.getExpensesByUser(user, pageable);
        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved", response));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all expenses for current user (no pagination)")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses(
            @AuthenticationPrincipal User user) {
        List<ExpenseResponse> response = expenseService.getAllExpensesByUser(user);
        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved", response));
    }
}
