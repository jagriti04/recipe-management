package com.manage.recipe.repository;

import com.manage.recipe.model.Ingredient;
import com.manage.recipe.model.Recipe;
import com.manage.recipe.model.RecipeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeRepositoryImpl implements RecipeFilterRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(RecipeRepositoryImpl.class);

    @Override
    public List<Recipe> findByDynamicFilters(RecipeType recipeType, Integer servings,
                                             List<String> includeIngredients,
                                             List<String> excludeIngredients,
                                             String searchInstructions) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> query = cb.createQuery(Recipe.class);
        Root<Recipe> recipeRoot = query.from(Recipe.class);
        Join<Recipe, Ingredient> ingredientJoin = recipeRoot.join("ingredients", JoinType.INNER);

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

        // Exclude Ingredients Filter
        if (excludeIngredients != null && !excludeIngredients.isEmpty()) {
            List<String> lowerCaseExcludeIngredients = excludeIngredients.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            // Sub-query to identify recipes with excluded ingredients
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Recipe> subRecipeRoot = subquery.from(Recipe.class);
            Join<Recipe, Ingredient> subIngredientJoin = subRecipeRoot.join("ingredients");

            subquery.select(subRecipeRoot.get("id"))
                    .where(cb.lower(subIngredientJoin.get("name")).in(lowerCaseExcludeIngredients));

            // Exclude recipes that match the sub-query
            predicates.add(cb.not(recipeRoot.get("id").in(subquery)));
        }

        logger.info("In filter recipeImp, predicates are: {} ", predicates);

        if (searchInstructions != null && !searchInstructions.isEmpty()) {
            predicates.add(cb.like(cb.lower(recipeRoot.get("instructions")), "%" + searchInstructions.toLowerCase() + "%"));
        }

        query.select(recipeRoot).where(cb.and(predicates.toArray(new Predicate[0]))).distinct(true);

        return entityManager.createQuery(query).getResultList();
    }
}
