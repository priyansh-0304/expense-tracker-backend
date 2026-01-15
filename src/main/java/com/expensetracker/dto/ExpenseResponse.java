package com.expensetracker.dto;

import java.time.LocalDate;

public class ExpenseResponse {

    public Long id;
    public String title;
    public Double amount;
    public String category;
    public LocalDate date;

    public ExpenseResponse(
            Long id,
            String title,
            Double amount,
            String category,
            LocalDate date
    ) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }
}