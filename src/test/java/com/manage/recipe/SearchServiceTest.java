package com.manage.recipe;

import com.manage.recipe.dto.RecipeResponse;
import com.manage.recipe.dto.RecipeResponseDT0;
import com.manage.recipe.model.Ingredient;
import com.manage.recipe.model.RecipeType;
import com.manage.recipe.model.Recipe;
import com.manage.recipe.repository.RecipeFilterRepository;
import com.manage.recipe.services.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private RecipeFilterRepository recipeFilterRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SearchService searchService;

    private Recipe sampleRecipe;

    private RecipeResponseDT0 sampleRecipeResponseDTO;

    @BeforeEach
    void setUp() {
        // Create a sample recipe for testing
        List<Ingredient> ingredients = new ArrayList<>();
        sampleRecipe = new Recipe(1L, "Pancakes", RecipeType.VEGETARIAN, 2, ingredients , "Mix and cook ingredients",
                LocalDateTime.now(), LocalDateTime.now());

        // Create a sample RecipeResponseDTO
        sampleRecipeResponseDTO = new RecipeResponseDT0();
        sampleRecipeResponseDTO.setName("Pancakes");
    }

    @Test
    void testFilterRecipesWithIncludeIngredients() {
        List<String> includeIngredients = List.of("sugar", "milk");

        // Mock repository response for include ingredients
        when(recipeFilterRepository.findByDynamicFilters(
                any(), any(), eq(includeIngredients), any(), any()
        )).thenReturn(List.of(sampleRecipe));

        // Mock ModelMapper behavior for converting Recipe to RecipeResponseDTO
        when(modelMapper.map(any(Recipe.class), eq(RecipeResponseDT0.class))).thenReturn(sampleRecipeResponseDTO);


        RecipeResponse response = searchService.filterRecipes(null, null, includeIngredients, null, null);

        assertEquals(1, response.getRecipes().size());
        assertEquals("Pancakes", response.getRecipes().get(0).getName());
        verify(recipeFilterRepository, times(1)).findByDynamicFilters(any(), any(), eq(includeIngredients), any(), any());
    }

    @Test
    void testFilterRecipesWithExcludeIngredients() {
        List<String> excludeIngredients = List.of("onion");

        // Mock repository response for exclude ingredients
        when(recipeFilterRepository.findByDynamicFilters(
                any(), any(), any(), eq(excludeIngredients), any()
        )).thenReturn(List.of(sampleRecipe));

        // Mock ModelMapper behavior for converting Recipe to RecipeResponseDTO
        when(modelMapper.map(any(Recipe.class), eq(RecipeResponseDT0.class))).thenReturn(sampleRecipeResponseDTO);


        RecipeResponse response = searchService.filterRecipes(null, null, null, excludeIngredients, null);

        assertEquals(1, response.getRecipes().size());
        assertEquals("Pancakes", response.getRecipes().get(0).getName());
        verify(recipeFilterRepository, times(1)).findByDynamicFilters(any(), any(), any(), eq(excludeIngredients), any());
    }

    @Test
    void testFilterRecipesByInstructions() {
        String searchInstructions = "cook";

        when(recipeFilterRepository.findByDynamicFilters(
                any(), any(), any(), any(), eq(searchInstructions)
        )).thenReturn(List.of(sampleRecipe));

        // Mock ModelMapper behavior for converting Recipe to RecipeResponseDTO
        when(modelMapper.map(any(Recipe.class), eq(RecipeResponseDT0.class))).thenReturn(sampleRecipeResponseDTO);


        RecipeResponse response = searchService.filterRecipes(null, null, null, null, searchInstructions);

        assertEquals(1, response.getRecipes().size());
        assertEquals("Pancakes", response.getRecipes().get(0).getName());
        verify(recipeFilterRepository, times(1)).findByDynamicFilters(any(), any(), any(), any(), eq(searchInstructions));
    }

    @Test
    void testFilterRecipesWithMultipleConditions() {
        RecipeType recipeType = RecipeType.VEGETARIAN;
        int servings = 2;
        List<String> includeIngredients = List.of("flour");
        String searchInstructions = "mix";

        // Mock repository response for multiple conditions
        when(recipeFilterRepository.findByDynamicFilters(
                eq(recipeType), eq(servings), eq(includeIngredients), any(), eq(searchInstructions)
        )).thenReturn(List.of(sampleRecipe));

        // Mock ModelMapper behavior for converting Recipe to RecipeResponseDTO
        when(modelMapper.map(any(Recipe.class), eq(RecipeResponseDT0.class))).thenReturn(sampleRecipeResponseDTO);


        RecipeResponse response = searchService.filterRecipes(recipeType, servings, includeIngredients, null, searchInstructions);

        assertEquals(1, response.getRecipes().size());
        assertEquals("Pancakes", response.getRecipes().get(0).getName());
        verify(recipeFilterRepository, times(1)).findByDynamicFilters(eq(recipeType), eq(servings), eq(includeIngredients), any(), eq(searchInstructions));
    }

    @Test
    void testFilterRecipesWithoutAnyConditions() {
        // Mock repository response for an empty filter
        when(recipeFilterRepository.findByDynamicFilters(
                any(), any(), any(), any(), any()
        )).thenReturn(List.of(sampleRecipe));

        // Mock ModelMapper behavior for converting Recipe to RecipeResponseDTO
        when(modelMapper.map(any(Recipe.class), eq(RecipeResponseDT0.class))).thenReturn(sampleRecipeResponseDTO);

        RecipeResponse response = searchService.filterRecipes(null, null, null, null, null);

        assertEquals(1, response.getRecipes().size());
        assertEquals("Pancakes", response.getRecipes().get(0).getName());
        verify(recipeFilterRepository, times(1)).findByDynamicFilters(any(), any(), any(), any(), any());
    }
}
