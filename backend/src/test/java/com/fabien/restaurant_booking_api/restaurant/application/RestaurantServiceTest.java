package com.fabien.restaurant_booking_api.restaurant.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.restaurant.domain.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

  @Mock
  private RestaurantRepository restaurantRepository;

  private RestaurantService restaurantService;

  @BeforeEach
  void setUp() {
    restaurantService = new RestaurantService(restaurantRepository);
  }

  @Test
  void findAll_should_return_all_restaurants() {
    //Given
    Restaurant inputRestaurant1 = createTestRestaurantWithId(1L, "Test a manger", "1 rue du test",
        "99-99-99-99-99");
    Restaurant inputRestaurant2 = createTestRestaurantWithId(2L, "Testage et délice",
        "3 rue du test",
        "99-99-99-99-98");
    when(restaurantRepository.findAll()).thenReturn(List.of(inputRestaurant1, inputRestaurant2));

    //When
    List<Restaurant> results = restaurantService.findAll();

    //Then
    assertThat(results).hasSize(2)
        .extracting(Restaurant::getId)
        .containsExactly(1L, 2L);

    assertThat(results)
        .extracting(Restaurant::getName)
        .containsExactly("Test a manger", "Testage et délice");

    assertThat(results)
        .extracting(Restaurant::getAddress)
        .containsExactly("1 rue du test", "3 rue du test");

    assertThat(results)
        .extracting(Restaurant::getPhoneNumber)
        .containsExactly("99-99-99-99-99", "99-99-99-99-98");

    verify(restaurantRepository).findAll();
  }

  @Test
  void findById_should_return_restaurant_when_exists() {
    //Given
    Restaurant inputRestaurant1 = createTestRestaurantWithId(1L, "Test a manger", "1 rue du test",
        "99-99-99-99-99");

    //When
    when(restaurantRepository.findById(1L)).thenReturn(Optional.of(inputRestaurant1));

    //Then
    Restaurant result = restaurantService.findById(1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);

    verify(restaurantRepository).findById(1L);
  }

  @Test
  void findById_should_throw_exception_when_not_exists() {
    //Given
    Long noExistId = 999L;
    //When
    when(restaurantRepository.findById(noExistId)).thenReturn(Optional.empty());

    final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
      restaurantService.findById(noExistId);
    });

    assertThat(exception.getMessage()).isEqualTo("Restaurant not found with id : " + noExistId);
    verify(restaurantRepository).findById(noExistId);
  }

  @Test
  void create_should_save_and_return_restaurant() {
    //Given
    Restaurant input = createTestRestaurant();
    Restaurant expected = createTestRestaurant();
    //When
    when(restaurantRepository.save(any(Restaurant.class))).thenReturn(expected);

    //Then
    Restaurant saved = restaurantService.create(input);

    assertThat(saved).isNotNull();
    assertThat(saved.getName()).isEqualTo(expected.getName());
    assertThat(saved.getAddress()).isEqualTo(expected.getAddress());
    assertThat(saved.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());

    verify(restaurantRepository).save(any(Restaurant.class));
  }

  @Test
  void update_should_verify_existence_and_save_restaurant() {
    //Given
    Restaurant input = createTestRestaurantWithId(1L);
    Restaurant expected = createTestRestaurant("Test a manger", "1 avenue test", "00-00-00-00-00");
    //When
    when(restaurantRepository.findById(1L)).thenReturn(Optional.of(input));
    when(restaurantRepository.save(any(Restaurant.class))).thenReturn(expected);

    //Then
    Restaurant saved = restaurantService.update(1L, expected);

    assertThat(saved).isNotNull();
    assertThat(saved.getId()).isEqualTo(expected.getId());
    assertThat(saved.getName()).isEqualTo(expected.getName());
    assertThat(saved.getAddress()).isEqualTo(expected.getAddress());
    assertThat(saved.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());

    verify(restaurantRepository).findById(1L);
    verify(restaurantRepository).save(any(Restaurant.class));
  }

  @Test
  void update_should_throw_exception_when_not_exists() {
    //Given
    Long noExistId = 999L;
    Restaurant input = createTestRestaurant();
    //When
    when(restaurantRepository.findById(noExistId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> restaurantService.update(noExistId, input))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Restaurant not found with id : " + noExistId);
    verify(restaurantRepository).findById(noExistId);
    verify(restaurantRepository, never()).save(any(Restaurant.class));
  }

  @Test
  void deleteById_should_verify_existence_and_delete() {
    //Given
    Restaurant input = createTestRestaurantWithId(1L);

    //When
    when(restaurantRepository.findById(1L)).thenReturn(Optional.of(input));

    //Then
    restaurantService.deleteById(1L);

    verify(restaurantRepository).findById(1L);
    verify(restaurantRepository).deleteById(1L);
  }

  @Test
  void deleteById_should_throw_exception_when_not_exists() {
    //Given
    Long noExistId = 999L;
    //When
    when(restaurantRepository.findById(noExistId)).thenReturn(Optional.empty());

    //Then
    assertThatThrownBy(() -> restaurantService.deleteById(noExistId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Restaurant not found with id : " + noExistId);
    verify(restaurantRepository).findById(noExistId);
    verify(restaurantRepository, never()).deleteById(1L);
  }

  private Restaurant createTestRestaurant(String name, String address, String phoneNumber) {
    Restaurant restaurant = new Restaurant();
    restaurant.setName(name);
    restaurant.setAddress(address);
    restaurant.setPhoneNumber(phoneNumber);

    return restaurant;
  }

  private Restaurant createTestRestaurantWithId(Long id, String name, String address,
      String phoneNumber) {
    Restaurant restaurant = createTestRestaurant(name, address, phoneNumber);
    restaurant.setId(id);

    return restaurant;
  }

  private Restaurant createTestRestaurant() {
    return createTestRestaurant("Test Restaurant", "12 rue Test", "99-99-99-99-99");
  }

  private Restaurant createTestRestaurantWithId(Long id) {
    return createTestRestaurantWithId(id, "Test Restaurant", "12 rue Test", "99-99-99-99-99");
  }
}