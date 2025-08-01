package com.fabien.restaurant_booking_api.booking.application;

import com.fabien.restaurant_booking_api.booking.domain.Booking;
import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;

public class BookingMapper {

  public static BookingResponse toResponse(Booking booking) {
    if (booking == null) {
      return null;
    }

    return new BookingResponse(
        booking.getId(),
        booking.getDiningTable(),
        booking.getCustomer(),
        booking.getTimeSlotType(),
        booking.getDate(),
        booking.getStatus()
    );
  }

  public static Booking toEntity(BookingRequest request) {
    if (request == null) {
      return null;
    }

    Customer customer = new Customer();

    if (request.getCustomerId() != null) {
      customer.setId(request.getCustomerId());
    } else {
      customer.setName(request.getCustomerName());
      customer.setEmail(request.getCustomerEmail());
      customer.setPhoneNumber(request.getCustomerPhoneNumber());
    }

    DiningTable diningTable = new DiningTable();
    diningTable.setId(request.getDiningTableId());

    Booking booking = new Booking();

    booking.setDiningTable(diningTable);
    booking.setCustomer(customer);
    booking.setTimeSlotType(request.getTimeSlotType());
    booking.setDate(request.getDate());
    booking.setStatus(request.getStatus());

    return booking;
  }
}
