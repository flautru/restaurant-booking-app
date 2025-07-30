package com.fabien.restaurant_booking_api.table.application;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestDiningTableWithId;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestRestaurantWithId;
import static org.assertj.core.api.Assertions.assertThat;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;
import org.junit.jupiter.api.Test;

class DiningTableMapperTest {

  @Test
  void toResponse_should_map_dining_table_to_response_when_dining_table_is_valid() {
    // Given
    Restaurant restaurant = createTestRestaurantWithId(1L, "Chez Test", "15 rue Test",
        "01-23-45-67-89");
    DiningTable diningTable = createTestDiningTableWithId(2L, restaurant, 6,
        DiningTableStatus.AVAILABLE);

    // When
    DiningTableResponse response = DiningTableMapper.toResponse(diningTable);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(2L);
    assertThat(response.capacity()).isEqualTo(6);
    assertThat(response.status()).isEqualTo(DiningTableStatus.AVAILABLE);

    assertThat(response.restaurant()).isNotNull();
    assertThat(response.restaurant().id()).isEqualTo(1L);
    assertThat(response.restaurant().name()).isEqualTo("Chez Test");
    assertThat(response.restaurant().address()).isEqualTo("15 rue Test");
    assertThat(response.restaurant().phoneNumber()).isEqualTo("01-23-45-67-89");
  }

  @Test
  void toResponse_should_return_null_when_dining_table_is_null() {
    // Given
    DiningTable diningTable = null;

    // When
    DiningTableResponse response = DiningTableMapper.toResponse(diningTable);

    // Then
    assertThat(response).isNull();
  }

  @Test
  void toEntity_should_map_request_to_dining_table_when_request_is_valid() {
    // Given
    DiningTableRequest request = createTestRequest(1L, 4, DiningTableStatus.MAINTENANCE);

    // When
    DiningTable diningTable = DiningTableMapper.toEntity(request);

    // Then
    assertThat(diningTable).isNotNull();
    assertThat(diningTable.getId()).isNull();
    assertThat(diningTable.getCapacity()).isEqualTo(4);
    assertThat(diningTable.getStatus()).isEqualTo(DiningTableStatus.MAINTENANCE);

    assertThat(diningTable.getRestaurant()).isNotNull();
    assertThat(diningTable.getRestaurant().getId()).isEqualTo(1L);
    assertThat(diningTable.getRestaurant().getName()).isNull();
    assertThat(diningTable.getRestaurant().getAddress()).isNull();
    assertThat(diningTable.getRestaurant().getPhoneNumber()).isNull();
  }

  @Test
  void toEntity_should_return_null_when_request_is_null() {
    // Given
    DiningTableRequest request = null;

    // When
    DiningTable diningTable = DiningTableMapper.toEntity(request);

    // Then
    assertThat(diningTable).isNull();
  }

  @Test
  void toResponse_should_handle_dining_table_with_null_restaurant() {
    // Given
    DiningTable diningTable = new DiningTable();
    diningTable.setId(1L);
    diningTable.setCapacity(2);
    diningTable.setStatus(DiningTableStatus.AVAILABLE);
    diningTable.setRestaurant(null); // Edge case

    // When
    DiningTableResponse response = DiningTableMapper.toResponse(diningTable);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.capacity()).isEqualTo(2);
    assertThat(response.status()).isEqualTo(DiningTableStatus.AVAILABLE);
    assertThat(response.restaurant()).isNull();
  }

  private DiningTableRequest createTestRequest(Long restaurantId, Integer capacity,
      DiningTableStatus status) {
    DiningTableRequest request = new DiningTableRequest();
    request.setRestaurantId(restaurantId);
    request.setCapacity(capacity);
    request.setStatus(status);
    return request;
  }
}