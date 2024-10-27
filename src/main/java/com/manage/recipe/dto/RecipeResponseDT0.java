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

    private Integer servings;

    private List<IngredientDTO> ingredients;

    private String instructions;

}
