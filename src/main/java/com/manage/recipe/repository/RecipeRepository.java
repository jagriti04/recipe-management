package com.manage.recipe.repository;

import com.manage.recipe.model.Recipe;
import com.manage.recipe.model.RecipeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>{
    List<Recipe> findByRecipeType(RecipeType recipeType);

    // Filtering by servings
    List<Recipe> findByServings(int servings);

    // Query for filtering recipes that contain specific word in instructions
    @Query("SELECT r FROM Recipe r WHERE r.instructions LIKE CONCAT('%', :keyword, '%')")
    List<Recipe> findByInstructionsContaining(@Param("keyword") String keyword);


    @Query("SELECT r FROM Recipe r JOIN r.ingredients i WHERE i.name IN :includeIngredients")
    List<Recipe> findByIncludeIngredients(@Param("includeIngredients") List<String> includeIngredients);

    @Query("SELECT r FROM Recipe r WHERE r.id NOT IN " +
            "(SELECT r2.id FROM Recipe r2 JOIN r2.ingredients i WHERE i.name IN :excludeIngredients)")
    List<Recipe> findByExcludeIngredients(@Param("excludeIngredients") List<String> excludeIngredients);

    List<Recipe> findByDynamicFilters(
            RecipeType recipeType, Integer servings, List<String> includeIngredients,
            List<String> excludeIngredients, String searchInstructions);

}
