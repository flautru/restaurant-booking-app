package com.fabien.restaurant_booking_api.restaurant.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RestaurantRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Test
  void save_should_generate_id_and_persist_restaurant() {
    // Given
    Restaurant restaurant = createTestRestaurant("Test a manger", "1 rue Testage",
        "99-99-99-99-99");

    //When
    Restaurant saved = restaurantRepository.save(restaurant);

    //Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Test a manger");
    assertThat(saved.getAddress()).isEqualTo("1 rue Testage");
    assertThat(saved.getPhoneNumber()).isEqualTo("99-99-99-99-99");
  }

  @Test
  void findById_should_return_restaurant_when_exists() {
    // Given
    Restaurant restaurant = createTestRestaurant("Test a manger", "1 rue Testage",
        "99-99-99-99-99");
    Restaurant saved = entityManager.persistAndFlush(restaurant);

    // When
    Optional<Restaurant> found = restaurantRepository.findById(saved.getId());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Test a manger");
  }

  @Test
  void findById_should_return_empty_when_restaurant_not_exists() {
    // Given
    Long nonExistentId = 999L;

    // When
    Optional<Restaurant> found = restaurantRepository.findById(nonExistentId);

    // Then
    assertThat(found).isEmpty();
  }

  private Restaurant createTestRestaurant(String name, String address, String phoneNumber) {
    Restaurant restaurant = new Restaurant();
    restaurant.setName(name);
    restaurant.setAddress(address);
    restaurant.setPhoneNumber(phoneNumber);
    return restaurant;
  }
}