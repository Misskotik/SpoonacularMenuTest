package org.example;

import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class SpoonacularMenuTest {


    private final String apiKey = "93fe923ceafe4111980a825f50442934";
    private final String hashKey = "6f87c8cd02b36a14b8204e2aea68a951ec31689f";

    // Task 1
    @Test
    void getRecipeHealthyPositiveTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("ignorePantry", "false")
                .when()
                .get("https://api.spoonacular.com/recipes/716426/information")
//                .prettyPeek ()
                .body()
                .jsonPath();
          assertThat(response.get("veryHealthy"), is(true));
          assertThat(response.get("cheap"), is(false));
        assertThat(response.get("veryPopular"), is(true));
        assertThat(response.get("weightWatcherSmartPoints"), equalTo(4));
        assertThat(response.get("spoonacularScore"), equalTo(100.0F));
    }



    @Test
    void getRecipeGlutenFreeIncludeNutritionPositiveTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("includeNutrition", "true")
                .when()
                .get("https://api.spoonacular.com/recipes/715415/information")
//                .prettyPeek ()
                .body()
                .jsonPath();
        assertThat(response.get("glutenFree"), is(true));
        assertThat(response.get("dairyFree"), is(true));
        assertThat(response.get("healthScore"), equalTo(73.0F));
        assertThat(response.get("pricePerServing"), equalTo(276.67F));
        assertThat(response.get("spoonacularScore"), equalTo(99.0F));
    }

    @Test
    void getRecipeKaleVeganPositiveTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("titleMatch", "Kale")
                .when()
                .get("https://api.spoonacular.com/recipes/644387/information")
                .body()
                .jsonPath();
        assertThat(response.get("glutenFree"), is(true));
        assertThat(response.get("vegan"), is(true));

        assertThat(response.get("pricePerServing"), equalTo(69.09F));

    }

    @Test
    void getRecipeGlutenFrIncludeNutritionPositiveTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("query", "salad")
                .when()
                .get("https://api.spoonacular.com/recipes/794349/information")
                .prettyPeek ()
                .body()
                .jsonPath();
        assertThat(response.get("veryPopular"), is(false));
        assertThat(response.get("dairyFree"), is(true));
        assertThat(response.get("sourceName"), equalTo("Food and Spice"));
        assertThat(response.get("healthScore"), equalTo(100.0F));
    }

    @Test
    void getRecipeWithQueryParametersNegativeTest() {
        given()
                .queryParam("apiKey", apiKey)
                .queryParam("minSugar", "no")
                .when()
                .get("https://api.spoonacular.com/recipes/complexSearch")
//                .prettyPeek()
                .then()
                .statusCode(404);
    }


    @Test
    void getRecipeWithBodyChecksInGivenPositiveTest() {
        given()
                .queryParam("apiKey", apiKey)
                .queryParam("includeNutrition", "false")
                .expect()
                .body("vegetarian", is(false))
                .body("vegan", is(false))
                .body("license", equalTo("CC BY-SA 3.0"))
                .body("pricePerServing", equalTo(163.15F))
                .body("extendedIngredients[0].aisle", equalTo("Milk, Eggs, Other Dairy"))
                .when()
                .get("https://api.spoonacular.com/recipes/716429/information");
    }

    @Test
    void postRecipeAsianCuisineTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("title", "Thai Soup")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
//                .prettyPeek ()
                .body()
                .jsonPath();

        assertThat(response.get("cuisine"), equalTo("Asian"));

    }

    @Test
    void postRecipeIngredientsCuisineTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam("ingredients", "1/2 cup grated Parmigiano Reggiano")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
//                .prettyPeek ()
                .body()
                .jsonPath();

        assertThat(response.get("cuisine"), equalTo("Mediterranean"));

    }

    @Test
    void postRecipeFrenchCuisineTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam ("title", "French Croque Monsieur")
                .queryParam("ingredients", "8 slices of sandwich bread, 2.5 cups grated Gruyere, 1/2 cup grated Parmigiano Reggiano, 4 slices of cooked ham, 2 tablespoons unsalted butter,  3 tablespoons flour,2 cups hot milk, Pinch of freshly grated nutmeg, Dijon mustard, Salt and pepper to taste")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
                .prettyPeek ()
                .body()
                .jsonPath();

        assertThat(response.get("cuisine"), equalTo("Mediterranean"));
        assertThat(response.get("cuisines[2]"), equalTo( "French"));
    }


    @Test
    void postStatusCode500NegativeLanguageTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam ("language", "fr")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
//                .prettyPeek ()
                .then()
                .statusCode(500)
                .extract()
                .jsonPath();


    }
    @Test
    void postRecipeLanguagePositiveCuisineTest() {
        JsonPath response = given()
                .queryParam("apiKey", apiKey)
                .queryParam ("language", "en")
                .when()
                .post("https://api.spoonacular.com/recipes/cuisine")
                .prettyPeek ()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        assertThat(response.get("confidence"), equalTo(0.0F));

    }
// Task 2

        @Test
    void addMealTest() {

        String id = given()
                .queryParam("hash", hashKey)
                .queryParam("apiKey", apiKey)
                .body("{\n"
                        + " \"date\": 1644881179,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"INGREDIENTS\",\n"
                        + " \"value\": {\n"
                        + " \"ingredients\": [\n"
                        + " {\n"
                        + " \"name\": \"1 avocado\"\n"
                        + " }\n"
                        + " ]\n"
                        + " }\n"
                        + "}")
                .when()
                .post("https://api.spoonacular.com/mealplanner/lally/items")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();

            given()
                    .queryParam("hash", hashKey)
                    .queryParam("apiKey", apiKey)
                    .when()
                    .get("https://api.spoonacular.com/mealplanner/lally/shopping-list")
                    .then()
                    .statusCode(200);
//                            .extract()
//                            .jsonPath()
//                            .get("id")
//                            .toString();

        given()
                .queryParam("hash", hashKey)
                .queryParam("apiKey", apiKey)
                .delete("https://api.spoonacular.com/mealplanner/lally/items/" + id)
                .then()
                .statusCode(200);
    }

}



