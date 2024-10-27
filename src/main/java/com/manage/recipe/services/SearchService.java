package com.manage.recipe.services;

import com.manage.recipe.dto.RecipeResponse;
import com.manage.recipe.dto.RecipeResponseDT0;
import com.manage.recipe.model.Recipe;
import com.manage.recipe.model.RecipeType;
import com.manage.recipe.repository.RecipeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public RecipeResponse filterRecipes(RecipeType recipeType, Integer servings,
                                              List<String> includeIngredients,
                                              List<String> excludeIngredients,
                                              String searchInstructions) {
        List<Recipe> recipes = new ArrayList<>();

        // Filters based on the parameters
        if (recipeType != null) {
            recipes.addAll(recipeRepository.findByRecipeType(recipeType));
        }
        if (servings != null) {
            recipes.addAll(recipeRepository.findByServings(servings));
        }
        if (includeIngredients != null && !includeIngredients.isEmpty()) {
            List<String> lowerCaseInIngredients = includeIngredients.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            recipes.addAll(recipeRepository.findByIncludeIngredients(lowerCaseInIngredients));
        }
        if (excludeIngredients != null && !excludeIngredients.isEmpty()) {
            List<String> lowerCaseExIngredients = excludeIngredients.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            recipes.addAll(recipeRepository.findByExcludeIngredients(lowerCaseExIngredients));
        }

        if (searchInstructions != null && !searchInstructions.isEmpty()) {
            recipes.addAll(recipeRepository.findByInstructionsContaining(searchInstructions));
        }

        // To remove duplicates if multiple filters yield the same recipe
        List <RecipeResponseDT0> recipeResponseList = recipes.stream()
                .distinct() // To avoid duplicates
                .map(recipe -> modelMapper.map(recipe, RecipeResponseDT0.class))
                .collect(Collectors.toList());
        RecipeResponse response = new RecipeResponse();

        response.setRecipes(recipeResponseList);
        response.setTotalRecipes(recipes.size());
        return response;

    }
}
