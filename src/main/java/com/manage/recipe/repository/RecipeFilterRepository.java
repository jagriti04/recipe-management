package com.manage.recipe.repository;

import com.manage.recipe.model.Recipe;
import com.manage.recipe.model.RecipeType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeFilterRepository {
    List<Recipe> findByDynamicFilters(
            RecipeType recipeType, Integer servings, List<String> includeIngredients,
            List<String> excludeIngredients, String searchInstructions);

}
