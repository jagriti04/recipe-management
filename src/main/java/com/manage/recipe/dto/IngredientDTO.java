package com.manage.recipe.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IngredientDTO {
    private Long id;
    @NotBlank(message = "Ingredient name is required and cannot be blank.")
    private String name;

    @NotNull(message = "Quantity is required.")
    @DecimalMin(value = "0.1", inclusive = true, message = "Quantity must be at least 0.1.")
    private Double quantity;

    @NotBlank(message = "Unit is required and cannot be blank.")
    private String unit;
}
