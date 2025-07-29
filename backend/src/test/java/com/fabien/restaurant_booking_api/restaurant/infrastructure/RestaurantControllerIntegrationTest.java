package com.fabien.restaurant_booking_api.restaurant.infrastructure;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.restaurant.domain.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests d'intégration pour RestaurantController.
 * <p>
 * Note : Pas de tests unitaires Controller séparés car : - Controller simple (orchestration
 * uniquement, pas de logique métier) - @MockBean déprécié depuis Spring Boot 3.4 - Tests
 * d'intégration couvrent le même périmètre (mapping, validation, HTTP codes) avec une valeur
 * ajoutée supérieure (test du flow complet)
 * <p>
 * Ces tests valident : - Sérialisation/désérialisation JSON - Validation @Valid des requêtes -
 * Codes de statut HTTP appropriés - Headers REST (Location pour POST) - Intégration Controller →
 * Service → Repository → Base de données
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RestaurantControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Test
  void findAll_should_return_all_restaurants_with_200() throws Exception {
    // Given
    Restaurant restaurant1 = createTestRestaurant("Chez Test", "15 rue Test", "01-11-11-11-11");
    Restaurant restaurant2 = createTestRestaurant("Le Testrot", "20 avenue Test", "01-22-22-22-22");

    restaurantRepository.save(restaurant1);
    restaurantRepository.save(restaurant2);

    // When & Then
    mockMvc.perform(get("/api/restaurants"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name", is("Chez Test")))
        .andExpect(jsonPath("$[0].address", is("15 rue Test")))
        .andExpect(jsonPath("$[0].phoneNumber", is("01-11-11-11-11")))
        .andExpect(jsonPath("$[1].name", is("Le Testrot")));
  }

  @Test
  void findAll_should_return_empty_list_when_no_restaurants() throws Exception {
    // Given

    // When & Then
    mockMvc.perform(get("/api/restaurants"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void findById_should_return_restaurant_with_200_when_exists() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Chez Test", "123 rue Test", "01-23-45-67-89");
    Restaurant saved = restaurantRepository.save(restaurant);

    // When & Then
    mockMvc.perform(get("/api/restaurants/{id}", saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
        .andExpect(jsonPath("$.name", is("Chez Test")))
        .andExpect(jsonPath("$.address", is("123 rue Test")))
        .andExpect(jsonPath("$.phoneNumber", is("01-23-45-67-89")));
  }

  @Test
  void findById_should_return_404_when_restaurant_not_exists() throws Exception {
    // Given
    Long nonExistentId = 999L;

    // When & Then
    mockMvc.perform(get("/api/restaurants/{id}", nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_should_create_restaurant_and_return_201_with_location() throws Exception {
    // Given
    String requestJson = """
        {
          "name": "Nouveau Restaurant",
          "address": "456 boulevard Nouveau",
          "phoneNumber": "01-98-76-54-32"
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/restaurants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", matchesPattern(".*/api/restaurants/\\d+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name", is("Nouveau Restaurant")))
        .andExpect(jsonPath("$.address", is("456 boulevard Nouveau")))
        .andExpect(jsonPath("$.phoneNumber", is("01-98-76-54-32")));
  }

  @Test
  void create_should_return_400_when_name_is_blank() throws Exception {
    // Given
    String requestJson = """
        {
          "name": "",
          "address": "123 rue Test",
          "phoneNumber": "01-23-45-67-89"
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/restaurants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_should_return_400_when_address_is_blank() throws Exception {
    // Given
    String requestJson = """
        {
          "name": "Restaurant Test",
          "address": "",
          "phoneNumber": "01-23-45-67-89"
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/restaurants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_should_return_400_when_phone_number_is_blank() throws Exception {
    // Given
    String requestJson = """
        {
          "name": "Restaurant Test",
          "address": "123 rue Test",
          "phoneNumber": ""
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/restaurants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_should_return_400_when_request_body_is_invalid_json() throws Exception {
    // Given
    String invalidJson = """
        {
          "name": "Restaurant Test",
          "address": "123 rue Test"
          // phoneNumber manquant + virgule manquante
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/restaurants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_should_update_restaurant_and_return_200() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Ancien Nom", "Ancienne Adresse",
        "01-11-11-11-11");
    Restaurant saved = restaurantRepository.save(restaurant);

    String updateJson = """
        {
          "name": "Nouveau Nom",
          "address": "Nouvelle Adresse",
          "phoneNumber": "01-22-22-22-22"
        }
        """;

    // When & Then
    mockMvc.perform(put("/api/restaurants/{id}", saved.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
        .andExpect(jsonPath("$.name", is("Nouveau Nom")))
        .andExpect(jsonPath("$.address", is("Nouvelle Adresse")))
        .andExpect(jsonPath("$.phoneNumber", is("01-22-22-22-22")));
  }

  @Test
  void update_should_return_404_when_restaurant_not_exists() throws Exception {
    // Given
    Long nonExistentId = 999L;
    String updateJson = """
        {
          "name": "Nouveau Nom",
          "address": "Nouvelle Adresse",
          "phoneNumber": "01-22-22-22-22"
        }
        """;

    // When & Then
    mockMvc.perform(put("/api/restaurants/{id}", nonExistentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_should_return_400_when_request_is_invalid() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Test", "Test", "01-11-11-11-11");
    Restaurant saved = restaurantRepository.save(restaurant);

    String invalidJson = """
        {
          "name": "",
          "address": "Nouvelle Adresse",
          "phoneNumber": "01-22-22-22-22"
        }
        """;

    // When & Then
    mockMvc.perform(put("/api/restaurants/{id}", saved.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void delete_should_delete_restaurant_and_return_204() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("À Supprimer", "123 rue Test", "01-11-11-11-11");
    Restaurant saved = restaurantRepository.save(restaurant);

    // When & Then
    mockMvc.perform(delete("/api/restaurants/{id}", saved.getId()))
        .andExpect(status().isNoContent());

    // Vérification que le restaurant est bien supprimé
    mockMvc.perform(get("/api/restaurants/{id}", saved.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_should_return_404_when_restaurant_not_exists() throws Exception {
    // Given
    Long nonExistentId = 999L;

    // When & Then
    mockMvc.perform(delete("/api/restaurants/{id}", nonExistentId))
        .andExpect(status().isNotFound());
  }

  private Restaurant createTestRestaurant(String name, String address, String phoneNumber) {
    Restaurant restaurant = new Restaurant();
    restaurant.setName(name);
    restaurant.setAddress(address);
    restaurant.setPhoneNumber(phoneNumber);
    return restaurant;
  }
}