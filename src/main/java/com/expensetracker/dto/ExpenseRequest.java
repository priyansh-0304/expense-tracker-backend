package com.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class ExpenseRequest {

    @NotBlank(message = "Title is required")
    public String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    public Double amount;

    @NotBlank(message = "Category is required")
    public String category;

    @NotNull(message = "Date is required")
    public LocalDate date;

    // 🔹 ADD THESE
    @NotBlank(message = "Payment method is required")
    public String paymentMethod;

    public String notes;
}