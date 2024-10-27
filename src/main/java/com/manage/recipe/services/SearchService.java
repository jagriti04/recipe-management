package com.manage.recipe.services;

import com.manage.recipe.dto.RecipeResponse;
import com.manage.recipe.dto.RecipeResponseDT0;
import com.manage.recipe.model.Recipe;
import com.manage.recipe.model.RecipeType;
import com.manage.recipe.repository.RecipeFilterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private RecipeFilterRepository recipeFilterRepository;

    @Autowired
    private ModelMapper modelMapper;

    public RecipeResponse filterRecipes(RecipeType recipeType, Integer servings,
                                        List<String> includeIngredients,
                                        List<String> excludeIngredients,
                                        String searchInstructions) {

        List<Recipe> recipes = recipeFilterRepository.findByDynamicFilters(
                recipeType, servings, includeIngredients, excludeIngredients, searchInstructions
        );

        List<RecipeResponseDT0> recipeResponseList = recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeResponseDT0.class))
                .collect(Collectors.toList());

        RecipeResponse response = new RecipeResponse();
        response.setRecipes(recipeResponseList);
        response.setTotalRecipes(recipeResponseList.size());
        return response;
    }
}
