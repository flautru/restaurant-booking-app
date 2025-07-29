package com.fabien.restaurant_booking_api.restaurant.application;

public record RestaurantResponse(
    Long id,
    String name,
    String address,
    String phoneNumber
) {

}
