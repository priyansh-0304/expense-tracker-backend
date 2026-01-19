package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.dto.ExpenseSummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    @Query("""
        SELECT e FROM Expense e
        WHERE e.user = :user
        AND (:category IS NULL OR e.category = :category)
        AND (:from IS NULL OR e.date >= :from)
        AND (:to IS NULL OR e.date <= :to)
    """)
    Page<Expense> findFilteredExpenses(
            @Param("user") User user,
            @Param("category") String category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable
    );

    @Query("""
        SELECT new com.expensetracker.dto.ExpenseSummaryResponse(
            e.category,
            SUM(e.amount)
        )
        FROM Expense e
        WHERE e.user = :user
        AND MONTH(e.date) = :month
        AND YEAR(e.date) = :year
        GROUP BY e.category
    """)
    List<ExpenseSummaryResponse> getMonthlySummary(
            @Param("user") User user,
            @Param("month") int month,
            @Param("year") int year
    );
    Optional<Expense> findByIdAndUser(Long id, User user);
}