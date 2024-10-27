package com.manage.recipe.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.manage.recipe.dto.*;
import com.manage.recipe.model.RecipeType;
import com.manage.recipe.services.RecipeManagementService;
import com.manage.recipe.services.SearchService;
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


    @GetMapping()
    public ResponseEntity<RecipeResponse> getAllRecipes(){
        return new ResponseEntity<>(recipeManagementService.getAllRecipes() , HttpStatus.OK) ;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<String>> addRecipe(@RequestBody RecipeRequestDTO recipeRequest) {
        ApiResponse<String> addedResponse = recipeManagementService.addRecipe(recipeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateAllRecipeDetails(
            @PathVariable Long id,
            @RequestBody RecipeRequestDTO updatedRecipeDTO) {
        ApiResponse<String> updatedResponse = recipeManagementService.updateRecipe(id, updatedRecipeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updatePartialRecipeDetails(@PathVariable Long id,
                                                                          @RequestBody RecipeUpdateRequestDTO updatedRecipeDTO) throws JsonMappingException {
        ApiResponse<String> updatedResponse = recipeManagementService.updatePartialRecipe(id, updatedRecipeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        recipeManagementService.deleteRecipe(id);
        return ResponseEntity.ok("Recipe deleted successfully");
    }

    @GetMapping("/search")
    public RecipeResponse filterRecipes(
            @RequestParam(required = false) RecipeType recipeType,
            @RequestParam(required = false) Integer servings,
            @RequestParam(required = false) List<String> includeIngredients,
            @RequestParam(required = false) List<String> excludeIngredients,
            @RequestParam(required = false) String searchInstructions) {
        return searchService.filterRecipes(recipeType, servings, includeIngredients, excludeIngredients, searchInstructions);
    }
}
