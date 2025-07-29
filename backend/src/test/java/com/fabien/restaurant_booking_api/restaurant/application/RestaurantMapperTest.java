package com.fabien.restaurant_booking_api.restaurant.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import org.junit.jupiter.api.Test;

class RestaurantMapperTest {

  @Test
  void toResponse_should_map_restaurant_to_response_when_restaurant_is_valid() {
    // Given
    Restaurant restaurant = createTestRestaurantWithId(1L, "Chez Testeur", "15 rue test",
        "00-00-00-00-00");

    // When
    RestaurantResponse response = RestaurantMapper.toResponse(restaurant);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.name()).isEqualTo("Chez Testeur");
    assertThat(response.address()).isEqualTo("15 rue test");
    assertThat(response.phoneNumber()).isEqualTo("00-00-00-00-00");
  }

  @Test
  void toResponse_should_return_null_when_restaurant_is_null() {
    // Given
    Restaurant restaurant = null;

    // When
    RestaurantResponse response = RestaurantMapper.toResponse(restaurant);

    // Then
    assertThat(response).isNull();
  }

  @Test
  void toEntity_should_map_request_to_restaurant_when_request_is_valid() {
    // Given
    RestaurantRequest request = createTestRequest("Le Testrot", "10 avenue Test", "99-99-99-99-99");

    // When
    Restaurant restaurant = RestaurantMapper.toEntity(request);

    // Then
    assertThat(restaurant).isNotNull();
    assertThat(restaurant.getId()).isNull();
    assertThat(restaurant.getName()).isEqualTo("Le Testrot");
    assertThat(restaurant.getAddress()).isEqualTo("10 avenue Test");
    assertThat(restaurant.getPhoneNumber()).isEqualTo("99-99-99-99-99");
  }

  @Test
  void toEntity_should_return_null_when_request_is_null() {
    // Given
    RestaurantRequest request = null;

    // When
    Restaurant restaurant = RestaurantMapper.toEntity(request);

    // Then
    assertThat(restaurant).isNull();
  }

  private RestaurantRequest createTestRequest(String name, String address, String phoneNumber) {
    RestaurantRequest request = new RestaurantRequest();
    request.setName(name);
    request.setAddress(address);
    request.setPhoneNumber(phoneNumber);

    return request;
  }

  // Duplication de code en local pour le moment a voir évolution pour faire un helper
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
}