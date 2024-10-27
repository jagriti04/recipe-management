package com.manage.recipe.repository;

import com.manage.recipe.model.Ingredient;
import com.manage.recipe.model.Recipe;
import com.manage.recipe.model.RecipeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeRepositoryImpl implements RecipeFilterRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Recipe> findByDynamicFilters(RecipeType recipeType, Integer servings,
                                             List<String> includeIngredients,
                                             List<String> excludeIngredients,
                                             String searchInstructions) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> query = cb.createQuery(Recipe.class);
        Root<Recipe> recipeRoot = query.from(Recipe.class);
        Join<Recipe, Ingredient> ingredientJoin = recipeRoot.join("ingredients", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (recipeType != null) {
            predicates.add(cb.equal(recipeRoot.get("recipeType"), recipeType));
        }
        if (servings != null) {
            predicates.add(cb.equal(recipeRoot.get("servings"), servings));
        }
        if (includeIngredients != null && !includeIngredients.isEmpty()) {
            // Convert includeIngredients to lowercase
            List<String> lowerCaseIncludeIngredients = includeIngredients.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            predicates.add(cb.lower(ingredientJoin.get("name")).in(lowerCaseIncludeIngredients));
        }

        if (excludeIngredients != null && !excludeIngredients.isEmpty()) {
            // Convert excludeIngredients to lowercase
            List<String> lowerCaseExcludeIngredients = excludeIngredients.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            predicates.add(cb.not(cb.lower(ingredientJoin.get("name")).in(lowerCaseExcludeIngredients)));
        }

        if (searchInstructions != null && !searchInstructions.isEmpty()) {
            predicates.add(cb.like(cb.lower(recipeRoot.get("instructions")), "%" + searchInstructions.toLowerCase() + "%"));
        }

        query.select(recipeRoot).where(cb.and(predicates.toArray(new Predicate[0]))).distinct(true);

        return entityManager.createQuery(query).getResultList();
    }
}
