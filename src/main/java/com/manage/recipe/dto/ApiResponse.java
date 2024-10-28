package com.manage.recipe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private String message;  // For success/failure messages like "Recipe added" or "Recipe updated"
    private T data;          // Can hold any type of data (e.g., Recipe list, Recipe details, etc.)
    private boolean success; // To indicate if the operation was successful

    public ApiResponse(String message, T data, boolean success) {
        this.message = message;
        this.data = data;
        this.success = success;
    }

}

