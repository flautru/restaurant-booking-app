package com.fabien.restaurant_booking_api.customer.application;

public record CustomerResponse(
    Long id,
    String phoneNumber,
    String email,
    String name
) {

}
