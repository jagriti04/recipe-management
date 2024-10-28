package com.manage.recipe.services;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.recipe.dto.*;
import com.manage.recipe.exception.ResourceNotFoundException;
import com.manage.recipe.model.Ingredient;
import com.manage.recipe.model.Recipe;
import com.manage.recipe.repository.RecipeRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeManagementService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(RecipeManagementService.class);

    public RecipeResponse getAllRecipes(){
        List<Recipe> recipes = recipeRepository.findAll();
        // Map the list of Recipe to RecipeResponseDTO
        List<RecipeResponseDT0> recipeResponseList = recipes.stream()
                .map(recipe -> modelMapper.map(recipe, RecipeResponseDT0.class))
                .collect(Collectors.toList());
        RecipeResponse response = new RecipeResponse();

        logger.info("GET method- response ingredients list is: {}", recipeResponseList);

        response.setRecipes(recipeResponseList);
        response.setTotalRecipes(recipes.size());
        return response;
    }


    // Method to add a new recipe
    public ApiResponse<String> addRecipe(RecipeRequestDTO recipeRequest) {
        Recipe recipe = modelMapper.map(recipeRequest, Recipe.class);
        List<Ingredient> ingredients = ingredientService.findOrCreateIngredientList(recipeRequest.getIngredients());
        recipe.setIngredients(ingredients);
        recipe.setCreatedAt(LocalDateTime.now());
        logger.info("Adding recipe and ingredients are {}", recipe.getIngredients());

        return saveRecipeToRepository(recipe, "Recipe successfully added");
    }


    // Method for update recipe - one or more or all fields update
    public ApiResponse<String> updateRecipe(Long id, RecipeUpdateRequestDTO updatedRecipeDTO) throws JsonMappingException {
        Recipe recipe = findRecipeById(id);

        // Handle ingredients
        List<Ingredient> updatedIngredients = null;
        if (updatedRecipeDTO.getIngredients()!= null && updatedRecipeDTO.getIngredients().isPresent()) {
            List<IngredientDTO> incomingIngredients = updatedRecipeDTO.getIngredients().get();

            // Get ingredients to remove
            List<String> ingredientsToRemove = null;
            if (updatedRecipeDTO.getRemoveIngredients() != null && !updatedRecipeDTO.getRemoveIngredients().isEmpty()) {
                ingredientsToRemove = updatedRecipeDTO.getRemoveIngredients().get();
            }
            updatedIngredients = ingredientService.mergeIngredients(recipe.getIngredients(), incomingIngredients, ingredientsToRemove);
        }

        logger.info("Partial updated request: {} and old recipe: {}", updatedRecipeDTO, recipe);

        objectMapper.updateValue(recipe, updatedRecipeDTO); // Updates specific fields in Recipe entity

        if(updatedIngredients != null) {
            recipe.setIngredients(updatedIngredients);
        }

        return saveRecipeToRepository(recipe, "Recipe's given fields updated");
    }

    // Method to delete a recipe by its id
    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipe not found with id: " + id);
        }
        recipeRepository.deleteById(id);
    }

    // Helper method to save a recipe and return a response
    private ApiResponse<String> saveRecipeToRepository(Recipe recipe, String successMessage) {
        recipe.setUpdatedAt(LocalDateTime.now()); // setting updated timestamp
        Recipe savedRecipe = recipeRepository.save(recipe);
        logger.info("Saving to repository: {}", savedRecipe.getName());
        return createApiResponse(savedRecipe, successMessage);
    }

    // Helper to find a recipe by ID and handle not found exception
    private Recipe findRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe with id: " + id + " does not exist"));
    }


    // Helper method to create an ApiResponse
    private ApiResponse<String> createApiResponse(Recipe recipe, String message) {
        RecipeResponseDT0 recipeResponse = modelMapper.map(recipe, RecipeResponseDT0.class);
        logger.info("API response: {}", recipeResponse);
        return new ApiResponse<>(message, recipeResponse.getName(), true);
    }


}
