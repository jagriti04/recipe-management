package com.manage.recipe;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.recipe.dto.*;
import com.manage.recipe.exception.ResourceNotFoundException;
import com.manage.recipe.model.Ingredient;
import com.manage.recipe.model.Recipe;
import com.manage.recipe.model.RecipeType;
import com.manage.recipe.repository.RecipeRepository;
import com.manage.recipe.services.IngredientService;
import com.manage.recipe.services.RecipeManagementService;
import com.manage.recipe.dto.RecipeResponseDT0;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeManagementServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecipeManagementService recipeManagementService;

    private Recipe existingRecipe;
    private RecipeResponseDT0 recipeResponseDTO;

    @BeforeEach
    void setUp() {
        // Initialize an existing recipe
        existingRecipe = new Recipe();
        existingRecipe.setId(1L);
        existingRecipe.setName("Old Tea");
        existingRecipe.setRecipeType(RecipeType.VEGETARIAN);
        existingRecipe.setServings(1);
        existingRecipe.setInstructions("Old instructions");

        Ingredient ingredient1 = new Ingredient("water", 150.0, "gram");
        Ingredient ingredient2 = new Ingredient("milk", 15.0, "ml");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);
        existingRecipe.setIngredients(ingredients);

        // Initialize response DTO
        recipeResponseDTO = new RecipeResponseDT0();
        recipeResponseDTO.setName("Updated Tea");
    }

    // Test if recipes are fetching successfully
    @Test
    void getAllRecipes_ShouldReturnRecipeResponse() {
        // Arrange
        Recipe recipe = new Recipe();
        recipe.setName("Tea");
        List<Recipe> recipes = List.of(recipe);

        RecipeResponseDT0 recipeResponseDTO = new RecipeResponseDT0();
        recipeResponseDTO.setName("Tea");

        when(recipeRepository.findAll()).thenReturn(recipes);
        when(modelMapper.map(recipe, RecipeResponseDT0.class)).thenReturn(recipeResponseDTO);

        // Act
        RecipeResponse response = recipeManagementService.getAllRecipes();

        // Assert
        assertEquals(1, response.getTotalRecipes());
        assertEquals("Tea", response.getRecipes().get(0).getName());
    }

    //To test recipes are added successfully
    @Test
    void addRecipe_ShouldSaveRecipeSuccessfully() {
        RecipeRequestDTO recipeRequest = new RecipeRequestDTO("Tea", RecipeType.VEGETARIAN, 1,
                List.of(new IngredientDTO(1L, "Tea bag", 1.0, "pack")), "Boil water and add tea bags");

        Recipe recipe = new Recipe();
        recipe.setName("Tea");

        // Creating a mock RecipeResponseDT0
        RecipeResponseDT0 recipeResponseDTO = new RecipeResponseDT0();
        recipeResponseDTO.setName("Tea");

        when(modelMapper.map(recipeRequest, Recipe.class)).thenReturn(recipe);
        when(ingredientService.findOrCreateIngredientList(recipeRequest.getIngredients())).thenReturn(Collections.emptyList());
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(modelMapper.map(recipe, RecipeResponseDT0.class)).thenReturn(recipeResponseDTO); // Mock the mapping return

        // Act
        ApiResponse<String> response = recipeManagementService.addRecipe(recipeRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Recipe successfully added", response.getMessage());
    }

    // Test if update function in service is working correctly
    @Test
    void updatePartialRecipe_ShouldUpdateRecipeSuccessfully_WhenValidRequest() throws JsonMappingException {
        Long existingId = 1L;
        RecipeUpdateRequestDTO updateRequestDTO = new RecipeUpdateRequestDTO();
        updateRequestDTO.setName(Optional.of("Updated Tea"));
        updateRequestDTO.setIngredients(Optional.of(Arrays.asList(new IngredientDTO(1L, "Tea bag", 1.0, "pack"))));

        // Mocking behavior
        when(recipeRepository.findById(existingId)).thenReturn(Optional.of(existingRecipe));

        // Mock the save method to return the updated existingRecipe
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe recipeToSave = invocation.getArgument(0);
            existingRecipe.setName(recipeToSave.getName()); // Updates the name based on incoming recipe
            existingRecipe.setIngredients(recipeToSave.getIngredients());
            return existingRecipe; // Returns the updated existingRecipe
        });

        // Mock the modelMapper behavior for mapping to response DTO
        RecipeResponseDT0 recipeResponseDTO = new RecipeResponseDT0();
        recipeResponseDTO.setName("Updated Tea"); // Sets expected name for response DTO
        when(modelMapper.map(existingRecipe, RecipeResponseDT0.class)).thenReturn(recipeResponseDTO); // Mock the mapping return

        // Mock the objectMapper to update the recipe object directly
        when(objectMapper.updateValue(any(Recipe.class), any(RecipeUpdateRequestDTO.class))).thenAnswer(invocation -> {
            Recipe recipe = invocation.getArgument(0);
            RecipeUpdateRequestDTO dto = invocation.getArgument(1);
            dto.getName().ifPresent(recipe::setName);
            return null;
        });

        ApiResponse<String> response = recipeManagementService.updateRecipe(existingId, updateRequestDTO);

        assertEquals("Recipe's given fields updated", response.getMessage());
        assertEquals("Updated Tea", existingRecipe.getName()); // Verify that the name was updated
    }

    // To test if correct error thrown if ID is not present in the database
    @Test
    void updatePartialRecipe_ShouldThrowResourceNotFoundException_WhenRecipeDoesNotExist() {
        Long nonExistentId = 999L; // ID that does not exist in the database

        // Mock the behavior of the repository to return empty for non-existent recipe
        when(recipeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            recipeManagementService.updateRecipe(nonExistentId, new RecipeUpdateRequestDTO());
        });
    }

    // To test delete method throws correct error if Id not present, in the service class
    @Test
    void deleteRecipe_ShouldThrowResourceNotFoundException_WhenRecipeDoesNotExist() {
        Long nonExistingId = 99L;

        when(recipeRepository.existsById(nonExistingId)).thenReturn(false); // Recipe does not exist

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            recipeManagementService.deleteRecipe(nonExistingId);
        });

        assertEquals("Recipe not found with id: " + nonExistingId, exception.getMessage());

        verify(recipeRepository, never()).deleteById(nonExistingId);
    }

    // To test delete method in the service class
    @Test
    void deleteRecipe_ShouldDeleteRecipe_WhenRecipeExists() {
        Long existingId = 1L;

        when(recipeRepository.existsById(existingId)).thenReturn(true); // Recipe exists

        recipeManagementService.deleteRecipe(existingId);

        verify(recipeRepository, times(1)).deleteById(existingId);
    }
}
