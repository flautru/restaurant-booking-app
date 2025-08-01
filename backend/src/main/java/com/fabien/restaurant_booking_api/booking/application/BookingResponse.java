package com.fabien.restaurant_booking_api.booking.application;

import com.fabien.restaurant_booking_api.booking.domain.BookingStatus;
import com.fabien.restaurant_booking_api.booking.domain.TimeSlotType;
import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import java.time.LocalDate;

public record BookingResponse(Long id, DiningTable table, Customer customer,
                              TimeSlotType timeSlotType, LocalDate date,
                              BookingStatus status) {

}
