package com.manage.recipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeUpdateRequestDTO {

    @JsonProperty("recipe_name")
    private Optional<String> name; // Optional to indicate field may be absent

    @JsonProperty("is_vegetarian")
    private Optional<Boolean> vegetarian; // Renamed JSON field to `is_vegetarian`

    @JsonProperty("serving_size")
    private Optional<Integer> servings;

    @JsonProperty("ingredients_list")
    private Optional<List<IngredientDTO>> ingredients;

    @JsonProperty("instructions")
    private Optional<String> instructions;
}
