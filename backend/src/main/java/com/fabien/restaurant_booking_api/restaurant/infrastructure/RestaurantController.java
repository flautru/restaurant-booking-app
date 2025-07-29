package com.fabien.restaurant_booking_api.restaurant.infrastructure;

import com.fabien.restaurant_booking_api.restaurant.application.RestaurantMapper;
import com.fabien.restaurant_booking_api.restaurant.application.RestaurantRequest;
import com.fabien.restaurant_booking_api.restaurant.application.RestaurantResponse;
import com.fabien.restaurant_booking_api.restaurant.application.RestaurantService;
import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin("http://localhost:4200")
@RequiredArgsConstructor
public class RestaurantController {

  private final RestaurantService restaurantService;

  @GetMapping
  public ResponseEntity<List<RestaurantResponse>> findAll() {
    List<RestaurantResponse> restaurantResponses = restaurantService.findAll()
        .stream()
        .map(RestaurantMapper::toResponse)
        .toList();
    return ResponseEntity.ok(restaurantResponses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RestaurantResponse> findById(@PathVariable Long id) {
    Restaurant restaurant = restaurantService.findById(id);

    return ResponseEntity.ok(RestaurantMapper.toResponse(restaurant));
  }

  @PostMapping
  public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody RestaurantRequest request) {
    Restaurant restaurant = RestaurantMapper.toEntity(request);

    RestaurantResponse response = RestaurantMapper.toResponse(restaurantService.create(restaurant));
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.id())
        .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RestaurantResponse> update(@PathVariable Long id,
      @Valid @RequestBody RestaurantRequest request) {
    Restaurant updatedRestaurant = restaurantService.update(id, RestaurantMapper.toEntity(request));

    return ResponseEntity.ok(RestaurantMapper.toResponse(updatedRestaurant));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    restaurantService.deleteById(id);

    return ResponseEntity.noContent().build();
  }
}
