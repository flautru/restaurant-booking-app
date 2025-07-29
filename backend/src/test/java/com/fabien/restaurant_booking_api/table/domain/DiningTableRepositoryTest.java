package com.fabien.restaurant_booking_api.table.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DiningTableRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private DiningTableRepository diningTableRepository;

  @Test
  void save_should_generate_id_and_persist_dining_table() {
    // Given
    Restaurant restaurant = createAndPersistRestaurant();
    DiningTable table = createTestDiningTable(restaurant, 4, DiningTableStatus.AVAILABLE);

    // When
    DiningTable saved = diningTableRepository.save(table);

    // Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCapacity()).isEqualTo(4);
    assertThat(saved.getStatus()).isEqualTo(DiningTableStatus.AVAILABLE);
  }

  @Test
  void save_should_persist_dining_table_with_restaurant_relation() {
    // Given
    Restaurant restaurant = createAndPersistRestaurant();
    DiningTable table = createTestDiningTable(restaurant, 6, DiningTableStatus.MAINTENANCE);

    // When
    DiningTable saved = diningTableRepository.save(table);

    // Then
    assertThat(saved.getRestaurant()).isNotNull();
    assertThat(saved.getRestaurant().getId()).isEqualTo(restaurant.getId());
    assertThat(saved.getRestaurant().getName()).isEqualTo("Test Restaurant");
  }

  @Test
  void findById_should_return_dining_table_when_exists() {
    // Given
    Restaurant restaurant = createAndPersistRestaurant();
    DiningTable table = createTestDiningTable(restaurant, 8, DiningTableStatus.AVAILABLE);
    DiningTable saved = entityManager.persistAndFlush(table);

    // When
    Optional<DiningTable> found = diningTableRepository.findById(saved.getId());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getCapacity()).isEqualTo(8);
    assertThat(found.get().getStatus()).isEqualTo(DiningTableStatus.AVAILABLE);
  }

  @Test
  void findById_should_return_empty_when_dining_table_not_exists() {
    // Given
    Long nonExistentId = 999L;

    // When
    Optional<DiningTable> found = diningTableRepository.findById(nonExistentId);

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  void findById_should_load_restaurant_relation_when_exists() {
    // Given
    Restaurant restaurant = createAndPersistRestaurant();
    DiningTable table = createTestDiningTable(restaurant, 2, DiningTableStatus.MAINTENANCE);
    DiningTable saved = entityManager.persistAndFlush(table);

    // When
    Optional<DiningTable> found = diningTableRepository.findById(saved.getId());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getRestaurant()).isNotNull();
    assertThat(found.get().getRestaurant().getName()).isEqualTo("Test Restaurant");
  }

  private Restaurant createTestRestaurant() {
    Restaurant restaurant = new Restaurant();
    restaurant.setName("Test Restaurant");
    restaurant.setAddress("123 Test St");
    restaurant.setPhoneNumber("99-99-99-99-99");
    return restaurant;
  }

  private Restaurant createAndPersistRestaurant() {
    Restaurant restaurant = createTestRestaurant();
    return entityManager.persistAndFlush(restaurant);
  }

  private DiningTable createTestDiningTable(Restaurant restaurant, int capacity,
      DiningTableStatus status) {
    DiningTable table = new DiningTable();
    table.setCapacity(capacity);
    table.setStatus(status);
    table.setRestaurant(restaurant);
    return table;
  }
}