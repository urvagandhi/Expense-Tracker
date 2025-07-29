package com.tracker.expense_tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracker.expense_tracker.entity.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndMonthAndYear(Long userId, int month, int year);

    List<Budget> findByUserId(Long userId);

    boolean existsByUserIdAndMonthAndYear(Long userId, int month, int year);
}
