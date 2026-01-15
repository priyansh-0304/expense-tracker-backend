package com.expensetracker.dto;

public class ExpenseSummaryResponse {

    private String category;
    private Double total;

    public ExpenseSummaryResponse(String category, Double total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public Double getTotal() {
        return total;
    }
}