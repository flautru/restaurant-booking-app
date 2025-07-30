package com.fabien.restaurant_booking_api.table.application;

import com.fabien.restaurant_booking_api.restaurant.application.RestaurantMapper;
import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;

public class DiningTableMapper {

  public static DiningTableResponse toResponse(DiningTable diningTable) {
    if (diningTable == null) {
      return null;
    }

    return new DiningTableResponse(
        diningTable.getId(),
        RestaurantMapper.toResponse(diningTable.getRestaurant()),
        diningTable.getCapacity(),
        diningTable.getStatus()
    );
  }

  public static DiningTable toEntity(DiningTableRequest request) {
    if (request == null) {
      return null;
    }

    DiningTable table = new DiningTable();
    table.setCapacity(request.getCapacity());
    table.setStatus(request.getStatus());

    Restaurant restaurant = new Restaurant();
    restaurant.setId(request.getRestaurantId());
    table.setRestaurant(restaurant);

    return table;
  }
}
