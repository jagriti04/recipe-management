package com.manage.recipe.dto;

import com.manage.recipe.model.RecipeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeRequestDTO {
    @NotBlank(message = "Name is required and cannot be blank.")
    private String name;

    @NotNull(message = "Recipe type is required.")
    private RecipeType recipeType;

    @NotNull(message = "Servings are required.")
    @Min(value = 1, message = "Servings must be at least 1.")
    private Integer servings;

    @NotEmpty(message = "Ingredients list cannot be empty.")
    private List<@Valid IngredientDTO> ingredients;

    @NotBlank(message = "Instructions are required and cannot be blank.")
    private String instructions;


}
