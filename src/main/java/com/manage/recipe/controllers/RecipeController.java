package com.manage.recipe.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.manage.recipe.dto.*;
import com.manage.recipe.model.RecipeType;
import com.manage.recipe.services.RecipeManagementService;
import com.manage.recipe.services.SearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeManagementService recipeManagementService;

    @Autowired
    private SearchService searchService;

    // Method to fetch details of all recipes
    @GetMapping()
    public ResponseEntity<RecipeResponse> getAllRecipes(){
        return new ResponseEntity<>(recipeManagementService.getAllRecipes() , HttpStatus.OK) ;
    }

    // Method to add a recipe in the database
    @PostMapping()
    public ResponseEntity<ApiResponse<String>> addRecipe(@RequestBody @Valid RecipeRequestDTO recipeRequest) {
        ApiResponse<String> addedResponse = recipeManagementService.addRecipe(recipeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedResponse);
    }

    // Method to update one or more fields of recipe, patch api is used.
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateRecipeDetails(@PathVariable Long id,
                                                                          @RequestBody @Valid RecipeUpdateRequestDTO updatedRecipeDTO) throws JsonMappingException {
        ApiResponse<String> updatedResponse = recipeManagementService.updateRecipe(id, updatedRecipeDTO);
        return ResponseEntity.ok(updatedResponse);
    }

    // Method to delete the recipe by getting it by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        recipeManagementService.deleteRecipe(id);
        return ResponseEntity.ok("Recipe deleted successfully");
    }

    // Method to search the recipe based upon request parameters
    // returns list of recipes with details
    @GetMapping("/search")
    public RecipeResponse filterRecipes(
            @RequestParam(required = false) @Valid RecipeType recipeType,
            @RequestParam(required = false) @Valid @Min(value = 1, message = "Servings must be at least 1.") Integer servings,
            @RequestParam(required = false) List<String> includeIngredients,
            @RequestParam(required = false) List<String> excludeIngredients,
            @RequestParam(required = false) String searchInstructions) {
        return searchService.filterRecipes(recipeType, servings, includeIngredients, excludeIngredients, searchInstructions);
    }
}
