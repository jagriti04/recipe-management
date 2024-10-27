markdown
Copy code
# Recipe Management API

## Overview

The Recipe Management API is a Spring Boot-based RESTful API designed for managing and filtering recipes and their ingredients.
Users can create, update, retrieve, and delete recipes, as well as filter them based on criteria such as whether the recipe is vegetarian, 
the number of servings, included/excluded ingredients, and search words within the instructions text.

## Features

- **Recipe Management**: Create, update, retrieve, and delete recipes.
- **Ingredient Management**: Each recipe can contain multiple ingredients with details.
- **Filtering Options**: Filter recipes based on various criteria such as:
    - Recipe Type having valid values as VEGAN, VEGETARIAN, NON_VEGETARIAN
    - Number of servings
    - Included and excluded ingredients
    - Text search within instructions

## Setup and Installation

### Prerequisites

- **Java 17** or higher
- **Maven** for dependency management
- **H2 Database** (In-memory database used for development)

### Installation

**Clone the Repository:**

```https://github.com/jagriti04/recipe-management```


**Build the Project:**

```mvn clean install ```

**Run the Application:**

```mvn spring-boot:run```

**Access the API:** 
Once the application is running, the API is accessible at:

```http://localhost:8080/api/recipes ```

### API Endpoints
Create Recipe: POST /api/recipes
Retrieve All Recipes: GET /api/recipes
Filter Recipes: GET /api/recipes/search with query parameters (e.g., ?servings=2&includeIngredients=tomato)
Update Recipe: PUT /api/recipes/{id}
Delete Recipe: DELETE /api/recipes/{id}

Example Requests
Filter Recipes:


GET /api/recipes/filter?servings=4&includeIngredients=cheese&excludeIngredients=meat&searchInstructions=grill

Request Body for Recipe Creation:

```JSON
{
    "name" : "Coffee vegan",
    "recipeType" : "VEGAN",
    "servings" : 5,
    "ingredients": [{"name": "coffee powder", "quantity": 5, "unit":"gram"},
          {"name": "sugar", "quantity": 10, "unit":"gram"} ],
    "instructions": "Boil and mix"
}
```

## Architecture and Technical Choices

1. Layered Architecture
   The project uses a layered architecture to maintain separation of concerns:

**Controller Layer:** Handles HTTP requests and responses.
**Service Layer:** Contains the business logic, including filtering operations.
**Repository Layer:** Manages data access with JPA Repositories.

2. Database Design
   Entities:
   Recipe with attributes like name, recipeType, servings, instructions, and a many-to-many relationship with Ingredient.
   Ingredient with attributes such as name, quantity, and unit.
   Database: An in-memory H2 database is used, with schema automatically generated based on JPA annotations.

3. Filtering Logic
   To handle complex filtering requirements:

Filters are managed using multiple query methods in the RecipeRepository.
Recipes are fetched based on each filter criteria, and then the final result set is obtained by intersecting these lists to ensure all criteria are met.

4. Key Libraries and Tools
   Spring Boot: Framework for creating RESTful APIs.
   ModelMapper: For mapping entities to DTOs for cleaner, more concise responses.
   H2 Database: In-memory database for easy setup and development.
   Lombok: For reducing boilerplate code by generating getters, setters, etc.

5. Handling Updates and Deletions
   For PUT (update) and DELETE operations:

The entity is first fetched by ID, modified if necessary, and then saved or deleted.
Partial updates are supported using PATCH, with checks to update only the provided fields.

#### Testing
Basic unit and integration tests are included using JUnit and Spring Boot Test.
H2 database configuration allows for isolated testing without affecting production data.

#### Future Enhancements
Add more advanced search capabilities, such as ingredient quantity-based filtering.
Implement user authentication for enhanced security.
Improve caching for frequently accessed recipes.
