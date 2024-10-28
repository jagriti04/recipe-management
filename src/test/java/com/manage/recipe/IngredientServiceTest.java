package com.manage.recipe;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.manage.recipe.dto.IngredientDTO;
import com.manage.recipe.model.Ingredient;
import com.manage.recipe.repository.IngredientRepository;
import com.manage.recipe.services.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    private List<Ingredient> existingIngredients;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existingIngredients = new ArrayList<>();
    }

    @Test
    void findOrCreateIngredient_ShouldReturnExistingIngredient_WhenIngredientExistsById() {
        Long existingId = 1L;
        IngredientDTO ingredientDTO = new IngredientDTO(existingId, "Sugar", 1.0, "cup");
        Ingredient existingIngredient = new Ingredient("sugar", 1.0, "cup");

        // Mock behavior
        when(ingredientRepository.findById(existingId)).thenReturn(Optional.of(existingIngredient));

        Ingredient result = ingredientService.findOrCreateIngredient(ingredientDTO);


        assertEquals(existingIngredient, result);
        verify(ingredientRepository, times(1)).findById(existingId);
        verify(ingredientRepository, never()).findByName(anyString());
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void findOrCreateIngredient_ShouldReturnExistingIngredient_WhenIngredientExistsByName() {
        String ingredientName = "salt";
        IngredientDTO ingredientDTO = new IngredientDTO(null, ingredientName, 1.0, "teaspoon");
        Ingredient existingIngredient = new Ingredient(ingredientName.toLowerCase(), 1.0, "teaspoon");

        when(ingredientRepository.findByName(ingredientName.toLowerCase())).thenReturn(Optional.of(existingIngredient));

        Ingredient result = ingredientService.findOrCreateIngredient(ingredientDTO);

        assertEquals(existingIngredient, result);
        verify(ingredientRepository, times(1)).findByName(ingredientName.toLowerCase());
        verify(ingredientRepository, never()).findById(anyLong());
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void findOrCreateIngredient_ShouldCreateNewIngredient_WhenNoExistingIngredient() {
        IngredientDTO ingredientDTO = new IngredientDTO(null, "flour", 1.0, "kg");

        // Mock behavior for both findById and findByName to return empty
        when(ingredientRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(ingredientRepository.findByName("flour")).thenReturn(Optional.empty());

        Ingredient newIngredient = new Ingredient("flour", 1.0, "kg");
        when(ingredientRepository.save(any())).thenReturn(newIngredient);

        Ingredient result = ingredientService.findOrCreateIngredient(ingredientDTO);

        assertEquals(newIngredient.getName(), result.getName());
        assertEquals(newIngredient.getQuantity(), result.getQuantity());
        assertEquals(newIngredient.getUnit(), result.getUnit());
        verify(ingredientRepository, times(1)).save(any());
    }

    @Test
    void mergeIngredients_ShouldUpdateExistingIngredient_WhenMatchingIdFound() {
        // Setup existing ingredients
        Ingredient existingIngredient = new Ingredient("Sugar", 1.0, "cup");
        existingIngredient.setId(1L);
        existingIngredients.add(existingIngredient);

        // Incoming ingredients
        List<IngredientDTO> incomingIngredients = Arrays.asList(new IngredientDTO(1L, "Sugar", 2.0, "kg"));

        List<Ingredient> result = ingredientService.mergeIngredients(existingIngredients, incomingIngredients, null);

        assertEquals(1, result.size());
        assertEquals(2.0, result.get(0).getQuantity()); // checking if quantity is updated
        assertEquals("kg", result.get(0).getUnit()); // checking if unit is updated
    }

    @Test
    void mergeIngredients_ShouldAddNewIngredient_WhenNoMatchingIdOrNameFound() {
        // Setup existing ingredients
        Ingredient existingIngredient = new Ingredient("Sugar", 1.0, "cup");
        existingIngredient.setId(1L);
        existingIngredients.add(existingIngredient);

        // Incoming ingredients
        List<IngredientDTO> incomingIngredients = Arrays.asList(new IngredientDTO(null, "Flour", 1.0, "kg"));

        List<Ingredient> result = ingredientService.mergeIngredients(existingIngredients, incomingIngredients, null);

        assertEquals(2, result.size()); // One existing and one new ingredient
        assertEquals("flour", result.get(1).getName()); // New ingredient name should be "Flour"
        assertEquals(1.0, result.get(1).getQuantity()); // Check quantity
    }

    @Test
    void mergeIngredients_ShouldUpdateExistingIngredient_WhenFoundByName() {
        // Define existing ingredients
        Ingredient existingIngredient = new Ingredient("Sugar", 1.0, "cup");
        existingIngredient.setId(1L);
        existingIngredients.add(existingIngredient);

        // Incoming ingredients
        List<IngredientDTO> incomingIngredients = Arrays.asList(new IngredientDTO(null, "sugar", 2.0, "kg"));

        List<Ingredient> result = ingredientService.mergeIngredients(existingIngredients, incomingIngredients, null);

        assertEquals(1, result.size());
        assertEquals(2.0, result.get(0).getQuantity()); // Quantity should be updated
        assertEquals("kg", result.get(0).getUnit()); // Unit should be updated
    }

    @Test
    void mergeIngredients_ShouldRemoveSpecifiedIngredients_WhenNamesMatched() {
        // Setup existing ingredients
        Ingredient existingIngredient1 = new Ingredient( "Sugar", 1.0, "cup");
        existingIngredient1.setId(1L);
        Ingredient existingIngredient2 = new Ingredient("Flour", 1.0, "kg");
        existingIngredient2.setId(2L);
        existingIngredients.add(existingIngredient1);
        existingIngredients.add(existingIngredient2);

        // Names of ingredients to remove
        List<String> removeIngredients = Collections.singletonList("sugar");

        List<Ingredient> result = ingredientService.mergeIngredients(existingIngredients, null, removeIngredients);

        assertEquals(1, result.size()); // Only one ingredient should remain
        assertEquals("Flour", result.get(0).getName()); // Flour should be the remaining ingredient
    }
}
