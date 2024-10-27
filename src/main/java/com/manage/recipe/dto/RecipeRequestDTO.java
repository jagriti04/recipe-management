package com.manage.recipe.dto;

import com.manage.recipe.model.RecipeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecipeRequestDTO {
    private String name;

    @Enumerated(EnumType.STRING)
    private RecipeType recipeType;

    private Integer servings;
    private List<IngredientDTO> ingredients;
    private String instructions;


}
