package com.manage.recipe.controllers;

import com.manage.recipe.dto.ApiResponse;
import com.manage.recipe.dto.RecipeRequestDTO;
import com.manage.recipe.dto.RecipeResponse;
import com.manage.recipe.dto.RecipeResponseDT0;
import com.manage.recipe.services.RecipeManagementService;
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
    public ResponseEntity<ApiResponse<String>> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeRequestDTO updatedRecipeDTO) {
        ApiResponse<String> addedResponse = recipeManagementService.updateRecipe(id, updatedRecipeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedResponse);
    }
}
