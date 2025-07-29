package com.fabien.restaurant_booking_api.restaurant.application;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;

public class RestaurantMapper {

  public static RestaurantResponse toResponse(Restaurant restaurant) {
    if (restaurant == null) {
      return null;
    }
    return new RestaurantResponse(
        restaurant.getId(),
        restaurant.getName(),
        restaurant.getAddress(),
        restaurant.getPhoneNumber());
  }

  public static Restaurant toEntity(RestaurantRequest request) {
    if (request == null) {
      return null;
    }

    Restaurant restaurant = new Restaurant();
    restaurant.setName(request.getName());
    restaurant.setAddress(request.getAddress());
    restaurant.setPhoneNumber(request.getPhoneNumber());

    return restaurant;
  }

}
