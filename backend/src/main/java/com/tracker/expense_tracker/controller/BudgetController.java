package com.tracker.expense_tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tracker.expense_tracker.dto.request.BudgetRequest;
import com.tracker.expense_tracker.dto.response.ApiResponse;
import com.tracker.expense_tracker.dto.response.BudgetResponse;
import com.tracker.expense_tracker.entity.User;
import com.tracker.expense_tracker.service.BudgetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets", description = "Budget management endpoints")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create a budget for a month")
    public ResponseEntity<ApiResponse<BudgetResponse>> addBudget(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.addBudget(user, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.updateBudget(id, user, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        budgetService.deleteBudget(id, user);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully"));
    }

    @GetMapping("/{month}/{year}")
    @Operation(summary = "Get budget for a specific month/year")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudget(
            @AuthenticationPrincipal User user,
            @PathVariable int month,
            @PathVariable int year) {
        BudgetResponse response = budgetService.getBudget(user, month, year);
        return ResponseEntity.ok(ApiResponse.success("Budget retrieved", response));
    }

    @GetMapping
    @Operation(summary = "Get all budgets for current user")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAllBudgets(
            @AuthenticationPrincipal User user) {
        List<BudgetResponse> response = budgetService.getAllBudgets(user);
        return ResponseEntity.ok(ApiResponse.success("Budgets retrieved", response));
    }
}
