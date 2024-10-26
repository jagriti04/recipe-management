package com.manage.recipe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecipeRequestDTO {
    private String name;
    private boolean isVegeterian;
    private Integer servings;
    private List<IngredientDTO> ingredients;
    private String instructions;


}
