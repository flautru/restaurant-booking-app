package com.fabien.restaurant_booking_api.restaurant.application;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.restaurant.domain.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

  private final RestaurantRepository restaurantRepository;

  public List<Restaurant> findAll() {
    return restaurantRepository.findAll();
  }

  public Restaurant findById(Long id) {
    return restaurantRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id : " + id));
  }

  public Restaurant create(Restaurant restaurant) {
    return restaurantRepository.save(restaurant);
  }

  public Restaurant update(Long id, Restaurant restaurant) {
    findById(id);
    restaurant.setId(id);
    return restaurantRepository.save(restaurant);
  }

  public void deleteById(Long id) {
    findById(id);

    restaurantRepository.deleteById(id);
  }
}
