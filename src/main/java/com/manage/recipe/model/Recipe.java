package com.manage.recipe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID
    private Long id;

    private String name;

    private Integer servings;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) // Define cascade and fetch type
    @JoinTable(
            name = "recipe_ingredient", // Join table to manage ManyToMany relationship
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredientList;

    @Lob // If instructions are large, we can store as a large object
    private String instructions;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
