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

        for(Recipe r: recipes){
            logger.info("GET method- db response: {}", r.getIngredients());
        }

        logger.info("GET method- response ingredients list is: {}", recipeResponseList);

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

        recipe.setIngredients(ingredients); // Set the ingredient list

        logger.info("Adding recipe and ingredients are {}", recipe.getIngredients());

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

        modelMapper.map(updatedRecipeDTO, recipe);

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

    public ApiResponse<String> updatePartialRecipe(Long id, RecipeUpdateRequestDTO updatedRecipeDTO) throws JsonMappingException {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person with uid" + id + "is not existed"));


        objectMapper.updateValue(recipe, updatedRecipeDTO);

        Recipe updatedRecipe = recipeRepository.save(recipe);

        RecipeResponseDT0 recipeResponse = modelMapper.map(updatedRecipe, RecipeResponseDT0.class);

        ApiResponse<String> response = new ApiResponse<>(
                "Recipe successfully updated",
                recipeResponse.getName(),
                true
        );
        return response;

    }

    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipe not found with id: " + id);
        }
        recipeRepository.deleteById(id);
    }
}
