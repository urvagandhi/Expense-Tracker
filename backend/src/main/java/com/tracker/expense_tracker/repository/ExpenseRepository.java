package com.tracker.expense_tracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tracker.expense_tracker.entity.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByUserId(Long userId, Pageable pageable);

    List<Expense> findByUserId(Long userId);

    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId " +
           "AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    Double sumByUserIdAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);
}
