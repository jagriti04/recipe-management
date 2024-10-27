package com.manage.recipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.manage.recipe.model.RecipeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeUpdateRequestDTO {
    private Optional<String> name;

    @Enumerated(EnumType.STRING)
    private Optional<RecipeType> recipeType;

    private Optional<Integer> servings;

    private Optional<List<IngredientDTO>> ingredients;

    private Optional<String> instructions;

    private Optional<List<String>> removeIngredients;   // List of ingredient names to remove
}
