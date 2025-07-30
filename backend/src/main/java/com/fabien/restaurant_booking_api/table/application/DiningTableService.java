package com.fabien.restaurant_booking_api.table.application;

import com.fabien.restaurant_booking_api.restaurant.application.RestaurantService;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import com.fabien.restaurant_booking_api.table.domain.DiningTableRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiningTableService {

  private final DiningTableRepository diningTableRepository;
  private final RestaurantService restaurantService;

  @Value("${restaurant.table.capacity.min}")
  private Integer minCapacity;

  @Value("${restaurant.table.capacity.max}")
  private Integer maxCapacity;

  public List<DiningTable> findAll() {
    return diningTableRepository.findAll();
  }

  public DiningTable findById(Long id) {
    return diningTableRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Dining table not found with id : " + id));
  }

  public DiningTable create(DiningTable diningTable) {
    validateCapacity(diningTable.getCapacity());
    restaurantService.validateExists(diningTable.getRestaurant().getId());

    return diningTableRepository.save(diningTable);

  }

  public DiningTable update(Long id, DiningTable diningTable) {
    findById(id);
    validateCapacity(diningTable.getCapacity());
    restaurantService.validateExists(diningTable.getRestaurant().getId());
    diningTable.setId(id);

    return diningTableRepository.save(diningTable);

  }

  public void deleteById(Long id) {
    findById(id);
    diningTableRepository.deleteById(id);
  }

  private void validateCapacity(Integer capacity) {
    if (capacity < minCapacity || capacity > maxCapacity) {
      throw new IllegalArgumentException(
          "La capacité doit être entre " + minCapacity + " et " + maxCapacity + " personnes");
    }
  }

}
