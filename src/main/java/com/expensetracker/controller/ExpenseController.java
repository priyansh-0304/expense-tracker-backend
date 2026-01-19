package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseSummaryResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import com.expensetracker.dto.ExpenseResponse;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepo;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepo) {
        this.expenseService = expenseService;
        this.userRepo = userRepo;
    }

    @PostMapping
    public Expense addExpense(@Valid @RequestBody ExpenseRequest request) {

        // get logged-in user's email from JWT
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense();
        expense.setTitle(request.title);
        expense.setAmount(request.amount);
        expense.setCategory(request.category);
        expense.setDate(request.date);
        expense.setUser(user);

        return expenseService.addExpense(expense);
    }

    @GetMapping
    public List<Expense> getMyExpenses() {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return expenseService.getExpensesForUser(user);
    }

    @GetMapping("/filter")
    public Page<ExpenseResponse> getFilteredExpenses(
            Authentication authentication,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                sortBy
        );

        return expenseService.getFilteredExpenseResponses(
                user,
                category,
                from,
                to,
                PageRequest.of(page, size, sort)
        );
    }
    @GetMapping("/summary")
    public List<ExpenseSummaryResponse> getMonthlySummary(
            Authentication authentication,
            @RequestParam int month,
            @RequestParam int year
    ) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return expenseService.getMonthlySummary(user, month, year);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow();

        expenseService.deleteExpense(user, id);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication
    ) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow();

        Expense updated = new Expense();
        updated.setTitle(request.title);
        updated.setAmount(request.amount);
        updated.setCategory(request.category);
        updated.setDate(request.date);

        return expenseService.updateExpense(user, id, updated);
    }
}