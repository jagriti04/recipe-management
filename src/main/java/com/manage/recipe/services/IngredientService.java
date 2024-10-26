package com.manage.recipe.services;

import com.manage.recipe.dto.IngredientDTO;
import com.manage.recipe.model.Ingredient;
import com.manage.recipe.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    public Ingredient findOrCreateIngredient(IngredientDTO ingredientDTO) {
        // Find ingredient by name or create a new one
        return ingredientRepository.findByName(ingredientDTO.getName())
                .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientDTO.getName(),
                        ingredientDTO.getQuantity(), ingredientDTO.getUnit())));
    }

}
