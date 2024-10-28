package com.manage.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.recipe.controllers.RecipeController;
import com.manage.recipe.dto.ApiResponse;
import com.manage.recipe.dto.IngredientDTO;
import com.manage.recipe.dto.RecipeRequestDTO;
import com.manage.recipe.dto.RecipeUpdateRequestDTO;
import com.manage.recipe.model.RecipeType;
import com.manage.recipe.services.RecipeManagementService;
import com.manage.recipe.services.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeManagementService recipeManagementService;

    @MockBean
    private SearchService searchService;

    private List<IngredientDTO> ingredients  = new ArrayList<>();

    @BeforeEach
    void setUp() {
        IngredientDTO ingredient1 = new IngredientDTO(1L, "Tea bag", 1.0, "pack");
        ingredients.add(ingredient1);
    }


    @Test
    void getAllRecipes_ShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes"))
                .andExpect(status().isOk());
    }

    @Test
    void addRecipe_ShouldReturnCreated() throws Exception {
        RecipeRequestDTO request = new RecipeRequestDTO("Tea", RecipeType.VEGETARIAN, 1, ingredients,
                "Boil water and add tea bag");

        Mockito.when(recipeManagementService.addRecipe(any(RecipeRequestDTO.class)))
                .thenReturn(new ApiResponse<>("Recipe added successfully",  request.getName(),true));

        // Convert request object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // To perform MockMvc request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Recipe added successfully"));
    }

    @Test
    void updatePartialRecipe_ShouldReturnOk() throws Exception {

        Mockito.when(recipeManagementService.updateRecipe(anyLong(), any(RecipeUpdateRequestDTO.class)))
                .thenReturn(new ApiResponse<>("Recipe updated successfully", "Coffee", true));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bread\"}" ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recipe updated successfully"));
    }

    @Test
    void deleteRecipe_ShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe deleted successfully"));
    }

    @Test
    void addRecipe_ShouldReturnBadRequest_WhenInvalidFieldValues() throws Exception {
        // an invalid RecipeRequestDTO with a negative serving size

        RecipeRequestDTO invalidRequest = new RecipeRequestDTO("Tea", RecipeType.VEGETARIAN, -1, ingredients,
                "Boil water and add tea bag");

        ObjectMapper objectMapper = new ObjectMapper();
        String invalidRequestJson = objectMapper.writeValueAsString(invalidRequest);

        // Perform the MockMvc request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addRecipe_ShouldReturnBadRequest_WhenMissingRequiredFields() throws Exception {
        // a RecipeRequestDTO with missing "name" and "instructions"
        RecipeRequestDTO invalidRequest = new RecipeRequestDTO(null, RecipeType.VEGETARIAN, 1, new ArrayList<>(), null);

        ObjectMapper objectMapper = new ObjectMapper();
        String invalidRequestJson = objectMapper.writeValueAsString(invalidRequest);

        // Perform the MockMvc request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }


}
