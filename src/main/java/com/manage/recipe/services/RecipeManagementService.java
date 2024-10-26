package com.manage.recipe.services;

import com.manage.recipe.dto.ApiResponse;
import com.manage.recipe.dto.RecipeRequestDTO;
import com.manage.recipe.dto.RecipeResponse;
import com.manage.recipe.dto.RecipeResponseDT0;
import com.manage.recipe.model.Ingredient;
import com.manage.recipe.model.Recipe;
import com.manage.recipe.repository.RecipeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeManagementService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IngredientService ingredientService;

    public RecipeResponse getAllRecipes(){
        List<Recipe> recipes = recipeRepository.findAll();
        // Map the list of Recipe to RecipeResponseDTO
        List<RecipeResponseDT0> recipeResponseList = recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeResponseDT0.class))
                .collect(Collectors.toList());
        RecipeResponse response = new RecipeResponse();
        response.setRecipes(recipeResponseList);
        response.setTotalRecipes(recipes.size());
        return response;
    }


    public ApiResponse<String> addRecipe(RecipeRequestDTO recipeRequest) {
        // Map RecipeRequestDTO to Recipe entity using ModelMapper
        Recipe recipe = modelMapper.map(recipeRequest, Recipe.class);

        // Handle ingredients separately
        List<Ingredient> ingredients = recipeRequest.getIngredients().stream()
                .map(ingredientDTO -> ingredientService.findOrCreateIngredient(ingredientDTO))
                .collect(Collectors.toList());

        recipe.setIngredientList(ingredients); // Set the ingredient list

        recipe.setCreatedAt(LocalDateTime.now()); // Set createdAt timestamp
        recipe.setUpdatedAt(LocalDateTime.now()); // Set updatedAt timestamp

        recipeRepository.save(recipe); // Save to the database

        ApiResponse<String> response = new ApiResponse<>(
                "Recipe successfully added",
                recipe.getName(),
                true
        );
        return response;
    }

    public ApiResponse<String> updateRecipe(Long id, RecipeRequestDTO updatedRecipeDTO) {
        // Check if the entity exists, if it doesn't then throw error
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe with id:" + id +"is not existed"));

        // Map the fields from the DTO to the existing recipe entity
        modelMapper.map(updatedRecipeDTO, recipe);

        // Set the updated timestamp and save the updated entity
        recipe.setUpdatedAt(LocalDateTime.now());
        Recipe updatedRecipe = recipeRepository.save(recipe);

        RecipeResponseDT0 recipeResponse = modelMapper.map(updatedRecipe, RecipeResponseDT0.class);

        ApiResponse<String> response = new ApiResponse<>(
                "Recipe successfully updated",
                recipeResponse.getName(),
                true
        );
        return response;
    }
}
