package com.fabien.restaurant_booking_api.booking.application;

import com.fabien.restaurant_booking_api.booking.domain.BookingStatus;
import com.fabien.restaurant_booking_api.booking.domain.TimeSlotType;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingRequest {

  private Long diningTableId;
  private Long customerId;
  private TimeSlotType timeSlotType;
  private LocalDate date;
  private BookingStatus status;

}