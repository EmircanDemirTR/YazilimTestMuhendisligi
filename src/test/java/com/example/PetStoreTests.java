package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetStoreTests {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    public void getAvailablePetsAndVerifyResponse() {
        given()
            .param("status", "available")
        .when()
            .get("/pet/findByStatus")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", greaterThan(0)) // Check if the array is not empty
            .body("[0].id", notNullValue())     // Check if the first pet has an id
            .body("[0].status", equalTo("available")) // Check if the first pet's status is 'available'
            .time(lessThan(2000L), TimeUnit.MILLISECONDS); // Check response time
    }

    @Test
    public void addNewPetAndVerifyResponse() {
        // Create a pet object for the request body
        Map<String, Object> pet = new HashMap<>();
        long petId = System.currentTimeMillis(); // Unique ID for the pet
        pet.put("id", petId);
        pet.put("name", "doggieTest");
        pet.put("status", "available");

        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", "Dogs");
        pet.put("category", category);

        pet.put("photoUrls", new String[]{"http://example.com/photo.jpg"});

        Map<String, Object> tag = new HashMap<>();
        tag.put("id", 0);
        tag.put("name", "testTag");
        pet.put("tags", new Map[]{tag});

        Response response = given()
            .contentType(ContentType.JSON)
            .body(pet)
            
        .when()
            .post("/pet")

        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(petId))
            .body("name", equalTo("doggieTest"))
            .body("status", equalTo("available"))
            .body("category.name", equalTo("Dogs"))
            .body("tags[0].name", equalTo("testTag"))
            .time(lessThan(1000L), TimeUnit.MILLISECONDS) // Check response time
            .extract().response();

        System.out.println("New Pet created with ID: " + response.jsonPath().getLong("id"));
    }
} 