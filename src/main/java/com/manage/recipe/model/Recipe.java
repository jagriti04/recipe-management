package com.manage.recipe.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private RecipeType recipeType;

    private Integer servings;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "recipe_ingredient", // Join table to manage ManyToMany relationship
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients;

    @Lob // If instructions are large, we can store as a large object
    private String instructions;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
