package com.manage.recipe.dto;

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
    public String name;
    public String type;
    public Integer servings;
    public List<IngredientDTO> ingredientsList;
    public String instructions;

}
