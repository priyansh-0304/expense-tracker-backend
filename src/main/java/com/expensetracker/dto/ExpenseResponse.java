package com.expensetracker.dto;

import java.time.LocalDate;

public class ExpenseResponse {

    public Long id;
    public String title;
    public Double amount;
    public String category;
    public LocalDate date;
    public String paymentMethod;
    public String notes;

    public ExpenseResponse(
            Long id,
            String title,
            Double amount,
            String category,
            LocalDate date,
            String paymentMethod,
            String notes
    ) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }
}