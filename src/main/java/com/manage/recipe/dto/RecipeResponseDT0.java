package com.manage.recipe.dto;

import com.manage.recipe.model.RecipeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponseDT0 {
    private Long id;
    public String name;
    @Enumerated(EnumType.STRING)
    private RecipeType recipeType;
    public Integer servings;
    public List<IngredientDTO> ingredientsList;
    public String instructions;

}
