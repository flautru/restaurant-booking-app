package com.fabien.restaurant_booking_api.table.infrastructure;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestDiningTable;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestRestaurant;
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
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import com.fabien.restaurant_booking_api.table.domain.DiningTableRepository;
import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests d'intégration pour DiningTableController.
 * <p>
 * Note : Pas de tests unitaires Controller séparés car : - Controller simple (orchestration
 * uniquement, pas de logique métier) - @MockBean déprécié depuis Spring Boot 3.4 - Tests
 * d'intégration couvrent le même périmètre (mapping, validation, HTTP codes) avec une valeur
 * ajoutée supérieure (test du flow complet)
 * <p>
 * Dans le contexte actuelle, transmition d'un Restaurant fantome au vu du contexte metier les
 * tables ne sont pas des choses qui vont souvent changer
 * <p>
 * Ces tests valident : - Sérialisation/désérialisation JSON - Validation @Valid des requêtes -
 * Codes de statut HTTP appropriés - Headers REST (Location pour POST) - Intégration Controller →
 * Service → Repository → Base de données
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DiningTableControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DiningTableRepository diningTableRepository;

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Test
  void findAll_should_return_all_dining_tables_with_200() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Chez Test", "15 rue Test", "01-11-11-11-11");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    DiningTable table1 = createTestDiningTable(savedRestaurant, 4, DiningTableStatus.AVAILABLE);
    DiningTable table2 = createTestDiningTable(savedRestaurant, 6, DiningTableStatus.MAINTENANCE);

    diningTableRepository.save(table1);
    diningTableRepository.save(table2);

    // When & Then
    mockMvc.perform(get("/api/tables"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].capacity", is(4)))
        .andExpect(jsonPath("$[0].status", is("AVAILABLE")))
        .andExpect(jsonPath("$[0].restaurant.name", is("Chez Test")))
        .andExpect(jsonPath("$[1].capacity", is(6)))
        .andExpect(jsonPath("$[1].status", is("MAINTENANCE")));
  }

  @Test
  void findAll_should_return_empty_list_when_no_tables() throws Exception {
    // When & Then
    mockMvc.perform(get("/api/tables"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void findById_should_return_dining_table_with_200_when_exists() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Restaurant Test", "123 rue Test",
        "01-23-45-67-89");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    DiningTable table = createTestDiningTable(savedRestaurant, 8, DiningTableStatus.AVAILABLE);
    DiningTable savedTable = diningTableRepository.save(table);

    // When & Then
    mockMvc.perform(get("/api/tables/{id}", savedTable.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(savedTable.getId().intValue())))
        .andExpect(jsonPath("$.capacity", is(8)))
        .andExpect(jsonPath("$.status", is("AVAILABLE")))
        .andExpect(jsonPath("$.restaurant.id", is(savedRestaurant.getId().intValue())))
        .andExpect(jsonPath("$.restaurant.name", is("Restaurant Test")));
  }

  @Test
  void findById_should_return_404_when_table_not_exists() throws Exception {
    // Given
    Long nonExistentId = 999L;

    // When & Then
    mockMvc.perform(get("/api/tables/{id}", nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_should_create_dining_table_and_return_201_with_location() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Nouveau Restaurant", "456 boulevard Test",
        "01-98-76-54-32");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    String requestJson = String.format("""
        {
          "restaurantId": %d,
          "capacity": 4,
          "status": "AVAILABLE"
        }
        """, savedRestaurant.getId());

    // When & Then
    mockMvc.perform(post("/api/tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", matchesPattern(".*/api/tables/\\d+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.capacity", is(4)))
        .andExpect(jsonPath("$.status", is("AVAILABLE")))
        .andExpect(jsonPath("$.restaurant.id", is(savedRestaurant.getId().intValue())));
  }

  @Test
  void create_should_return_404_when_restaurant_not_exists() throws Exception {
    // Given
    Long nonExistentRestaurantId = 999L;
    String requestJson = String.format("""
        {
          "restaurantId": %d,
          "capacity": 4,
          "status": "AVAILABLE"
        }
        """, nonExistentRestaurantId);

    // When & Then
    mockMvc.perform(post("/api/tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_should_return_500_when_capacity_below_min() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Test Restaurant", "123 rue Test",
        "01-11-11-11-11");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    String requestJson = String.format("""
        {
          "restaurantId": %d,
          "capacity": 1,
          "status": "AVAILABLE"
        }
        """, savedRestaurant.getId());

    // When & Then
    mockMvc.perform(post("/api/tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_should_return_400_when_capacity_above_max() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Test Restaurant", "123 rue Test",
        "01-11-11-11-11");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    String requestJson = String.format("""
        {
          "restaurantId": %d,
          "capacity": 10,
          "status": "AVAILABLE"
        }
        """, savedRestaurant.getId());

    // When & Then
    mockMvc.perform(post("/api/tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_should_return_400_when_restaurant_id_is_null() throws Exception {
    // Given
    String requestJson = """
        {
          "capacity": 4,
          "status": "AVAILABLE"
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_should_return_400_when_capacity_is_null() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Test Restaurant", "123 rue Test",
        "01-11-11-11-11");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    String requestJson = String.format("""
        {
          "restaurantId": %d,
          "status": "AVAILABLE"
        }
        """, savedRestaurant.getId());

    // When & Then
    mockMvc.perform(post("/api/tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_should_return_400_when_status_is_null() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Test Restaurant", "123 rue Test",
        "01-11-11-11-11");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    String requestJson = String.format("""
        {
          "restaurantId": %d,
          "capacity": 4
        }
        """, savedRestaurant.getId());

    // When & Then
    mockMvc.perform(post("/api/tables")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_should_update_dining_table_and_return_200() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Restaurant Initial", "Ancienne Adresse",
        "01-11-11-11-11");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    DiningTable table = createTestDiningTable(savedRestaurant, 4, DiningTableStatus.AVAILABLE);
    DiningTable savedTable = diningTableRepository.save(table);

    String updateJson = String.format("""
        {
          "restaurantId": %d,
          "capacity": 6,
          "status": "MAINTENANCE"
        }
        """, savedRestaurant.getId());

    // When & Then
    mockMvc.perform(put("/api/tables/{id}", savedTable.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(savedTable.getId().intValue())))
        .andExpect(jsonPath("$.capacity", is(6)))
        .andExpect(jsonPath("$.status", is("MAINTENANCE")))
        .andExpect(jsonPath("$.restaurant.id", is(savedRestaurant.getId().intValue())));
  }

  @Test
  void update_should_return_404_when_table_not_exists() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("Test Restaurant", "123 rue Test",
        "01-11-11-11-11");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    Long nonExistentId = 999L;
    String updateJson = String.format("""
        {
          "restaurantId": %d,
          "capacity": 4,
          "status": "AVAILABLE"
        }
        """, savedRestaurant.getId());

    // When & Then
    mockMvc.perform(put("/api/tables/{id}", nonExistentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_should_delete_dining_table_and_return_204() throws Exception {
    // Given
    Restaurant restaurant = createTestRestaurant("À Supprimer", "123 rue Test", "01-11-11-11-11");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    DiningTable table = createTestDiningTable(savedRestaurant, 4, DiningTableStatus.AVAILABLE);
    DiningTable savedTable = diningTableRepository.save(table);

    // When & Then
    mockMvc.perform(delete("/api/tables/{id}", savedTable.getId()))
        .andExpect(status().isNoContent());

    // Vérification que la table est bien supprimée
    mockMvc.perform(get("/api/tables/{id}", savedTable.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_should_return_404_when_table_not_exists() throws Exception {
    // Given
    Long nonExistentId = 999L;

    // When & Then
    mockMvc.perform(delete("/api/tables/{id}", nonExistentId))
        .andExpect(status().isNotFound());
  }
}