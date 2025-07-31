package com.fabien.restaurant_booking_api.booking.application;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestBookingWithId;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomerWithId;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestDiningTableWithId;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestRestaurantWithId;
import static org.assertj.core.api.Assertions.assertThat;

import com.fabien.restaurant_booking_api.booking.domain.Booking;
import com.fabien.restaurant_booking_api.booking.domain.BookingStatus;
import com.fabien.restaurant_booking_api.booking.domain.TimeSlotType;
import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.table.application.DiningTableMapper;
import com.fabien.restaurant_booking_api.table.application.DiningTableRequest;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BookingMapperTest {

  @Test
  void toResponse_should_map_booking_to_response_when_booking_is_valid() {
    // Given
    Restaurant restaurant = createTestRestaurantWithId(1L, "Chez Test", "15 rue Test",
        "01-23-45-67-89");
    DiningTable diningTable = createTestDiningTableWithId(2L, restaurant, 6,
        DiningTableStatus.AVAILABLE);

    Customer customer = createTestCustomerWithId(1L);

    Booking booking = createTestBookingWithId(1L, diningTable, customer, LocalDate.of(2025, 7, 31),
        TimeSlotType.DINNER_19H21H, BookingStatus.FINISH);
    // When
    BookingResponse response = BookingMapper.toResponse(booking);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.table()).isEqualTo(diningTable);
    assertThat(response.customer()).isEqualTo(customer);
    assertThat(response.localDate()).isEqualTo(LocalDate.of(2025, 7, 31));
    assertThat(response.timeSlotType()).isEqualTo(TimeSlotType.DINNER_19H21H);
    assertThat(response.bookingStatus()).isEqualTo(BookingStatus.FINISH);

  }

  @Test
  void toResponse_should_return_null_when_dining_table_is_null() {
    // When
    BookingResponse response = BookingMapper.toResponse(null);

    // Then
    assertThat(response).isNull();
  }

  @Test
  void toEntity_should_map_request_to_dining_table_when_request_is_valid() {
    // Given
    BookingRequest request = createTestRequest(1L, 1L, TimeSlotType.DINNER_19H21H,
        LocalDate.of(2025, 7, 31), BookingStatus.IN_PROGRESS);

    // When
    Booking booking = BookingMapper.toEntity(request);

    // Then
    assertThat(booking).isNotNull();
    assertThat(booking.getDiningTable().getId()).isEqualTo(request.getDiningTableId());
    assertThat(booking.getCustomer().getId()).isEqualTo(request.getCustomerId());
    assertThat(booking.getTimeSlotType()).isEqualTo(request.getTimeSlotType());
    assertThat(booking.getDate()).isEqualTo(request.getDate());
    assertThat(booking.getStatus()).isEqualTo(request.getStatus());

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

  private BookingRequest createTestRequest(Long tableId, Long customerId, TimeSlotType timeSlotType,
      LocalDate localDate, BookingStatus status) {
    BookingRequest request = new BookingRequest();
    request.setDiningTableId(tableId);
    request.setCustomerId(customerId);
    request.setTimeSlotType(timeSlotType);
    request.setDate(localDate);
    request.setStatus(status);

    return request;
  }

}