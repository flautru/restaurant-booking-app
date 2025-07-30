package com.fabien.restaurant_booking_api.table.application;

import com.fabien.restaurant_booking_api.restaurant.application.RestaurantResponse;
import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;

public record DiningTableResponse(
    Long id,
    RestaurantResponse restaurant,
    Integer capacity,
    DiningTableStatus status
) {

}
