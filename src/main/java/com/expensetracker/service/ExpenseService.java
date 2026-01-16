package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.ExpenseSummaryResponse;
import com.expensetracker.dto.ExpenseRequest;

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
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getTitle(),
                        expense.getAmount(),
                        expense.getCategory(),
                        expense.getDate()
                ));
    }

    // ---------- UPDATE ----------

    public Expense updateExpense(Long expenseId, ExpenseRequest request, User user) {
        Expense expense = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // ownership check (VERY important)
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this expense");
        }

        expense.setTitle(request.title);
        expense.setAmount(request.amount);
        expense.setCategory(request.category);
        expense.setDate(request.date);

        return expenseRepo.save(expense);
    }

    // ---------- DELETE ----------

    public void deleteExpense(Long expenseId, User user) {
        Expense expense = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // ownership check
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }

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