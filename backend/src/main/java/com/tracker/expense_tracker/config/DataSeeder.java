package com.tracker.expense_tracker.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tracker.expense_tracker.entity.*;
import com.tracker.expense_tracker.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        log.info("Seeding database with demo data...");

        // ===== User 1: Urva Gandhi (primary user) =====
        User urva = userRepository.save(User.builder()
                .username("Urva Gandhi")
                .email("urvagandhi24@gmail.com")
                .password(passwordEncoder.encode("Urva@2004"))
                .role(Role.USER)
                .createdAt(LocalDateTime.of(2025, 7, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 7, 1, 10, 0))
                .build());

        // Urva's expenses - spread across July and early August 2025
        List<Expense> urvaExpenses = expenseRepository.saveAll(List.of(
                expense(urva, 12.50, "Food", "Morning coffee & bagel", LocalDate.of(2025, 7, 2)),
                expense(urva, 45.00, "Transport", "Uber to office", LocalDate.of(2025, 7, 3)),
                expense(urva, 89.99, "Shopping", "New headphones", LocalDate.of(2025, 7, 5)),
                expense(urva, 32.00, "Food", "Lunch with team", LocalDate.of(2025, 7, 6)),
                expense(urva, 150.00, "Bills", "Electricity bill", LocalDate.of(2025, 7, 7)),
                expense(urva, 25.00, "Entertainment", "Movie tickets", LocalDate.of(2025, 7, 8)),
                expense(urva, 18.75, "Food", "Groceries", LocalDate.of(2025, 7, 10)),
                expense(urva, 60.00, "Health", "Gym membership", LocalDate.of(2025, 7, 11)),
                expense(urva, 200.00, "Shopping", "Summer clothes", LocalDate.of(2025, 7, 12)),
                expense(urva, 15.00, "Food", "Dinner takeout", LocalDate.of(2025, 7, 14)),
                expense(urva, 35.00, "Transport", "Metro pass reload", LocalDate.of(2025, 7, 15)),
                expense(urva, 120.00, "Education", "Online course - React", LocalDate.of(2025, 7, 16)),
                expense(urva, 42.50, "Food", "Weekend brunch", LocalDate.of(2025, 7, 18)),
                expense(urva, 75.00, "Bills", "Internet bill", LocalDate.of(2025, 7, 19)),
                expense(urva, 28.00, "Entertainment", "Spotify + Netflix", LocalDate.of(2025, 7, 20)),
                expense(urva, 55.00, "Food", "Grocery run", LocalDate.of(2025, 7, 22)),
                expense(urva, 300.00, "Shopping", "Laptop stand & keyboard", LocalDate.of(2025, 7, 23)),
                expense(urva, 22.00, "Transport", "Cab to airport", LocalDate.of(2025, 7, 24)),
                expense(urva, 40.00, "Food", "Restaurant dinner", LocalDate.of(2025, 7, 26)),
                expense(urva, 95.00, "Health", "Doctor visit", LocalDate.of(2025, 7, 27)),
                expense(urva, 16.00, "Food", "Coffee shop", LocalDate.of(2025, 7, 28)),
                expense(urva, 65.00, "Entertainment", "Concert tickets", LocalDate.of(2025, 7, 29)),
                // August expenses
                expense(urva, 38.00, "Food", "Lunch delivery", LocalDate.of(2025, 8, 1)),
                expense(urva, 110.00, "Bills", "Phone bill", LocalDate.of(2025, 8, 2)),
                expense(urva, 85.00, "Shopping", "Books - System Design", LocalDate.of(2025, 8, 3)),
                expense(urva, 20.00, "Transport", "Bus pass", LocalDate.of(2025, 8, 5)),
                expense(urva, 48.00, "Food", "Grocery haul", LocalDate.of(2025, 8, 6)),
                expense(urva, 60.00, "Health", "Gym membership renewal", LocalDate.of(2025, 8, 7))
        ));

        // Urva's budgets
        double julyTotal = urvaExpenses.stream()
                .filter(e -> e.getExpenseDate().getMonthValue() == 7)
                .mapToDouble(Expense::getAmount).sum();
        double augTotal = urvaExpenses.stream()
                .filter(e -> e.getExpenseDate().getMonthValue() == 8)
                .mapToDouble(Expense::getAmount).sum();

        budgetRepository.saveAll(List.of(
                budget(urva, 7, 2025, 2000.00, julyTotal),
                budget(urva, 8, 2025, 1800.00, augTotal)
        ));

        // ===== User 2: Raj Patel =====
        User raj = userRepository.save(User.builder()
                .username("Raj Patel")
                .email("raj.patel@example.com")
                .password(passwordEncoder.encode("Raj@1234"))
                .role(Role.USER)
                .createdAt(LocalDateTime.of(2025, 7, 5, 14, 0))
                .updatedAt(LocalDateTime.of(2025, 7, 5, 14, 0))
                .build());

        List<Expense> rajExpenses = expenseRepository.saveAll(List.of(
                expense(raj, 250.00, "Bills", "Rent contribution", LocalDate.of(2025, 7, 1)),
                expense(raj, 80.00, "Food", "Weekly groceries", LocalDate.of(2025, 7, 4)),
                expense(raj, 35.00, "Transport", "Gas refill", LocalDate.of(2025, 7, 7)),
                expense(raj, 120.00, "Shopping", "Running shoes", LocalDate.of(2025, 7, 10)),
                expense(raj, 55.00, "Food", "Pizza night with friends", LocalDate.of(2025, 7, 12)),
                expense(raj, 200.00, "Education", "AWS certification prep", LocalDate.of(2025, 7, 15)),
                expense(raj, 45.00, "Entertainment", "Gaming subscription", LocalDate.of(2025, 7, 18)),
                expense(raj, 90.00, "Food", "Groceries + snacks", LocalDate.of(2025, 7, 21)),
                expense(raj, 30.00, "Transport", "Parking fees", LocalDate.of(2025, 7, 25)),
                expense(raj, 150.00, "Health", "Dental checkup", LocalDate.of(2025, 7, 28)),
                expense(raj, 70.00, "Food", "Meal prep supplies", LocalDate.of(2025, 8, 2)),
                expense(raj, 40.00, "Entertainment", "Book - Clean Code", LocalDate.of(2025, 8, 5))
        ));

        double rajJuly = rajExpenses.stream()
                .filter(e -> e.getExpenseDate().getMonthValue() == 7)
                .mapToDouble(Expense::getAmount).sum();
        double rajAug = rajExpenses.stream()
                .filter(e -> e.getExpenseDate().getMonthValue() == 8)
                .mapToDouble(Expense::getAmount).sum();

        budgetRepository.saveAll(List.of(
                budget(raj, 7, 2025, 1500.00, rajJuly),
                budget(raj, 8, 2025, 1200.00, rajAug)
        ));

        // ===== User 3: Priya Sharma =====
        User priya = userRepository.save(User.builder()
                .username("Priya Sharma")
                .email("priya.sharma@example.com")
                .password(passwordEncoder.encode("Priya@2025"))
                .role(Role.USER)
                .createdAt(LocalDateTime.of(2025, 7, 8, 9, 0))
                .updatedAt(LocalDateTime.of(2025, 7, 8, 9, 0))
                .build());

        List<Expense> priyaExpenses = expenseRepository.saveAll(List.of(
                expense(priya, 180.00, "Shopping", "Skincare products", LocalDate.of(2025, 7, 3)),
                expense(priya, 65.00, "Food", "Organic groceries", LocalDate.of(2025, 7, 6)),
                expense(priya, 500.00, "Education", "UX Design bootcamp", LocalDate.of(2025, 7, 9)),
                expense(priya, 40.00, "Transport", "Ola rides", LocalDate.of(2025, 7, 12)),
                expense(priya, 95.00, "Entertainment", "Art supplies", LocalDate.of(2025, 7, 15)),
                expense(priya, 30.00, "Food", "Cafe working session", LocalDate.of(2025, 7, 18)),
                expense(priya, 75.00, "Health", "Yoga class package", LocalDate.of(2025, 7, 22)),
                expense(priya, 110.00, "Bills", "Cloud storage + tools", LocalDate.of(2025, 7, 25)),
                expense(priya, 55.00, "Food", "Dinner out", LocalDate.of(2025, 7, 29))
        ));

        double priyaJuly = priyaExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        budgetRepository.save(budget(priya, 7, 2025, 1800.00, priyaJuly));

        // ===== Admin User =====
        userRepository.save(User.builder()
                .username("Admin")
                .email("admin@tracker.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.of(2025, 7, 1, 8, 0))
                .updatedAt(LocalDateTime.of(2025, 7, 1, 8, 0))
                .build());

        log.info("Seed complete: 4 users, {} expenses, {} budgets",
                expenseRepository.count(), budgetRepository.count());
    }

    private Expense expense(User user, double amount, String category, String description, LocalDate date) {
        return Expense.builder()
                .user(user)
                .amount(amount)
                .category(category)
                .description(description)
                .expenseDate(date)
                .createdAt(date.atTime(10, 0))
                .updatedAt(date.atTime(10, 0))
                .build();
    }

    private Budget budget(User user, int month, int year, double limit, double totalExpense) {
        return Budget.builder()
                .user(user)
                .month(month)
                .year(year)
                .budgetLimit(limit)
                .totalExpense(totalExpense)
                .createdAt(LocalDateTime.of(year, month, 1, 10, 0))
                .updatedAt(LocalDateTime.of(year, month, 1, 10, 0))
                .build();
    }
}
