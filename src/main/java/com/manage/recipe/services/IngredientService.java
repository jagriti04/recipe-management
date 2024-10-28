package com.manage.recipe.services;

import com.manage.recipe.dto.IngredientDTO;
import com.manage.recipe.model.Ingredient;
import com.manage.recipe.repository.IngredientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    private static final Logger logger = LoggerFactory.getLogger(IngredientService.class);

    public Ingredient findOrCreateIngredient(IngredientDTO ingredientDTO) {
        // Convert the ingredient name to lowercase for consistency
        String ingredientName = ingredientDTO.getName().toLowerCase();

        if (ingredientDTO.getId() != null) {
            // Try to find the ingredient by ID first
            return ingredientRepository.findById(ingredientDTO.getId())
                    .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName,
                            ingredientDTO.getQuantity(), ingredientDTO.getUnit())));
        }

        // If no ID, find ingredient by name or create a new one
        return ingredientRepository.findByName(ingredientName)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName,
                        ingredientDTO.getQuantity(), ingredientDTO.getUnit())));
    }

    // Helper method to create or fetch ingredient list
    public List<Ingredient> findOrCreateIngredientList(List<IngredientDTO> ingredientDTOs) {
        return ingredientDTOs.stream()
                .map(this::findOrCreateIngredient)
                .collect(Collectors.toList());
    }

    public List<Ingredient> mergeIngredients(List<Ingredient> existingIngredients,
                                             List<IngredientDTO> incomingIngredients,
                                             List<String> removeIngredients) {
        logger.info("MERGING Ingredients - existing: {}, incoming: {}, remove:{} ", existingIngredients,
                incomingIngredients,removeIngredients);

        // Convert `existingIngredients` into a map for easy lookup by ID and name
        Map<Long, Ingredient> existingIngredientMapById = existingIngredients.stream()
                .collect(Collectors.toMap(Ingredient::getId, ingredient -> ingredient));

        Map<String, Ingredient> existingIngredientMapByName = existingIngredients.stream()
                .collect(Collectors.toMap(ingredient -> ingredient.getName().toLowerCase(), ingredient -> ingredient));

        // To update existing ingredients and adding new ones
        if (incomingIngredients != null) {
            for (IngredientDTO incoming : incomingIngredients) {
                // Checking if the ingredient has an ID for update
                if (incoming.getId() != null && existingIngredientMapById.containsKey(incoming.getId())) {
                    Ingredient existing = existingIngredientMapById.get(incoming.getId());
                    // Updating existing ingredient
                    if(incoming.getQuantity() != null) {
                        existing.setQuantity(incoming.getQuantity());
                    }
                    if(incoming.getUnit() != null) {
                        existing.setUnit(incoming.getUnit());
                    }
                    if(incoming.getName() != null) {
                        existing.setName(incoming.getName().toLowerCase()); // Ensure name consistency
                    }

                }
                // If no ID is present, look by name to update or add as new ingredient
                else {
                    String ingredientName = incoming.getName().toLowerCase();
                    Ingredient existingByName = existingIngredientMapByName.get(ingredientName);

                    if (existingByName != null) {
                        // Updating by name if it exists
                        if(incoming.getQuantity() != null) {
                            existingByName.setQuantity(incoming.getQuantity());
                        }
                        if(incoming.getUnit() != null) {
                            existingByName.setUnit(incoming.getUnit());
                        }
                    } else {
                        // Adding as new ingredient
                        Ingredient newIngredient = new Ingredient(incoming.getName().toLowerCase(),
                                incoming.getQuantity(),
                                incoming.getUnit());
                        existingIngredients.add(newIngredient);
                        existingIngredientMapByName.put(ingredientName, newIngredient); // Add to map for reference
                    }
                }
            }
        }

        // Removing ingredients specified in `removeIngredients`
        if (removeIngredients != null && !removeIngredients.isEmpty()) {
            existingIngredients.removeIf(ingredient ->
                    removeIngredients.stream().anyMatch(removeName ->
                            removeName.equalsIgnoreCase(ingredient.getName())));
        }
        logger.info("Final ingredients list: {}", existingIngredients);

        return existingIngredients;
    }



}
