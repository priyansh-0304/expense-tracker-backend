package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.ExpenseSummaryResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepo;

    public ExpenseService(ExpenseRepository expenseRepo) {
        this.expenseRepo = expenseRepo;
    }

    // ---------- CREATE ----------
    public Expense addExpense(Expense expense) {
        return expenseRepo.save(expense);
    }

    // ---------- READ BASIC ----------
    public List<Expense> getExpensesForUser(User user) {
        return expenseRepo.findByUser(user);
    }

    // ---------- FILTER (ENTITY) ----------
    public Page<Expense> getFilteredExpenses(
            User user,
            String category,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        return expenseRepo.findFilteredExpenses(user, category, from, to, pageable);
    }

    // ---------- FILTER (DTO) ----------
    public Page<ExpenseResponse> getFilteredExpenseResponses(
            User user,
            String category,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        return expenseRepo
                .findFilteredExpenses(user, category, from, to, pageable)
                .map(e -> new ExpenseResponse(
                        e.getId(),
                        e.getTitle(),
                        e.getAmount(),
                        e.getCategory(),
                        e.getDate(),
                        e.getPaymentMethod(),
                        e.getNotes()
                ));
    }

    // ---------- UPDATE ----------
    public Expense updateExpense(User user, Long id, Expense updated) {
        Expense expense = expenseRepo
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new AccessDeniedException("Not allowed"));

        expense.setTitle(updated.getTitle());
        expense.setAmount(updated.getAmount());
        expense.setCategory(updated.getCategory());
        expense.setDate(updated.getDate());
        expense.setPaymentMethod(updated.getPaymentMethod());
        expense.setNotes(updated.getNotes());

        return expenseRepo.save(expense);
    }

    // ---------- DELETE ----------
    public void deleteExpense(User user, Long id) {
        Expense expense = expenseRepo
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new AccessDeniedException("Not allowed"));

        expenseRepo.delete(expense);
    }

    // ---------- SUMMARY ----------
    public List<ExpenseSummaryResponse> getMonthlySummary(
            User user,
            int month,
            int year
    ) {
        return expenseRepo.getMonthlySummary(user, month, year);
    }
}