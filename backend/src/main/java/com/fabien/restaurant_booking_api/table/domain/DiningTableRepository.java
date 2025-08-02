package com.fabien.restaurant_booking_api.table.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {

  List<DiningTable> findByRestaurantId(
      Long restaurantId
  );

}
