package com.fabien.restaurant_booking_api.restaurant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.restaurant.domain.RestaurantRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

  @Mock
  private RestaurantRepository restaurantRepository;

  private RestaurantService restaurantService;

  @BeforeEach
  void setUp() {
    restaurantService = new RestaurantService(restaurantRepository);
  }

  @Test
  void findAll_should_return_all_restaurants() {
    //Given
    Restaurant inputRestaurant1 = createTestRestaurantWithId(1L, "Test a manger", "1 rue du test",
        "99-99-99-99-99");
    Restaurant inputRestaurant2 = createTestRestaurantWithId(2L, "Testage et délice",
        "3 rue du test",
        "99-99-99-99-98");
    when(restaurantRepository.findAll()).thenReturn(List.of(inputRestaurant1, inputRestaurant2));

    //When
    List<Restaurant> results = restaurantService.findAll();

    //Then
    assertThat(results).hasSize(2)
        .extracting(Restaurant::getId)
        .containsExactly(1L, 2L);

    assertThat(results)
        .extracting(Restaurant::getName)
        .containsExactly("Test a manger", "Testage et délice");

    assertThat(results)
        .extracting(Restaurant::getAddress)
        .containsExactly("1 rue du test", "3 rue du test");

    assertThat(results)
        .extracting(Restaurant::getPhoneNumber)
        .containsExactly("99-99-99-99-99", "99-99-99-99-98");
  }

  @Test
  void findById_should_return_restaurant_when_exists() {
  }

  @Test
  void findById_should_throw_exception_when_not_exists() {
  }

  @Test
  void create_should_save_and_return_restaurant() {
  }

  @Test
  void update_should_verify_existence_and_save_restaurant() {
  }

  @Test
  void update_should_throw_exception_when_not_exists() {
  }

  @Test
  void deleteById_should_verify_existence_and_delete() {
  }

  @Test
  void deleteById_should_throw_exception_when_not_exists() {

  }

  private Restaurant createTestRestaurant(String name, String address, String phoneNumber) {
    Restaurant restaurant = new Restaurant();
    restaurant.setName(name);
    restaurant.setAddress(address);
    restaurant.setPhoneNumber(phoneNumber);

    return restaurant;
  }

  private Restaurant createTestRestaurantWithId(Long id, String name, String address,
      String phoneNumber) {
    Restaurant restaurant = createTestRestaurant(name, address, phoneNumber);
    restaurant.setId(id);

    return restaurant;
  }

  private Restaurant createTestRestaurant() {
    return createTestRestaurant("Test Restaurant", "12 rue Test", "99-99-99-99-99");
  }

  private Restaurant createTestRestaurantWithId(Long id) {
    return createTestRestaurantWithId(id, "Test Restaurant", "12 rue Test", "99-99-99-99-99");
  }
}