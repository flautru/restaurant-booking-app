package com.fabien.restaurant_booking_api.table.application;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestDiningTable;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestDiningTableWithId;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestRestaurant;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestRestaurantWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fabien.restaurant_booking_api.restaurant.application.RestaurantService;
import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import com.fabien.restaurant_booking_api.table.domain.DiningTableRepository;
import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DiningTableServiceTest {

  @Mock
  private DiningTableRepository diningTableRepository;

  @Mock
  private RestaurantService restaurantService;

  private DiningTableService diningTableService;

  @BeforeEach
  void setUp() {
    diningTableService = new DiningTableService(diningTableRepository, restaurantService);

    ReflectionTestUtils.setField(diningTableService, "minCapacity", 2);
    ReflectionTestUtils.setField(diningTableService, "maxCapacity", 8);
  }

  @Test
  void findAll_should_return_all_dining_tables() {
    // Given
    Restaurant restaurant = createTestRestaurantWithId(1L);
    DiningTable table1 = createTestDiningTableWithId(1L, restaurant, 4,
        DiningTableStatus.AVAILABLE);
    DiningTable table2 = createTestDiningTableWithId(2L, restaurant, 8,
        DiningTableStatus.MAINTENANCE);

    // When
    when(diningTableRepository.findAll()).thenReturn(List.of(table1, table2));

    List<DiningTable> results = diningTableService.findAll();
    // Then
    assertThat(results)
        .hasSize(2)
        .extracting(DiningTable::getId)
        .containsExactly(1L, 2L);

    assertThat(results)
        .extracting(DiningTable::getCapacity)
        .containsExactly(4, 8);

    assertThat(results)
        .extracting(DiningTable::getStatus)
        .containsExactly(DiningTableStatus.AVAILABLE, DiningTableStatus.MAINTENANCE);

    verify(diningTableRepository).findAll();
  }

  @Test
  void findAll_should_return_empty_list_when_no_dining_tables() {
    // Given
    when(diningTableRepository.findAll()).thenReturn(List.of());

    // When
    List<DiningTable> results = diningTableService.findAll();

    // Then
    assertThat(results).isEmpty();

    verify(diningTableRepository).findAll();
  }

  @Test
  void findById_should_return_dining_table_when_exists() {
    // Given
    Restaurant restaurant = createTestRestaurantWithId(1L);
    DiningTable table1 = createTestDiningTableWithId(1L, restaurant, 4,
        DiningTableStatus.AVAILABLE);

    // When
    when(diningTableRepository.findById(1L)).thenReturn(Optional.of(table1));

    DiningTable result = diningTableService.findById(1L);

    assertThat(result).isNotNull();
    assertThat(result.getCapacity()).isEqualTo(4);
    assertThat(result.getStatus()).isEqualTo(DiningTableStatus.AVAILABLE);
    assertThat(result.getRestaurant().getId()).isEqualTo(restaurant.getId());

    verify(diningTableRepository).findById(1L);
  }

  @Test
  void findById_should_return_throw_exception_when_not_exists() {
    // Given
    Long nonExistentId = 999L;

    // When
    when(diningTableRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> diningTableService.findById(nonExistentId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Dining table not found with id : " + nonExistentId);

    verify(diningTableRepository).findById(nonExistentId);
  }

  @Test
  void create_should_save_and_return_dining_table() {
    // Given
    DiningTable input = createTestDiningTable();
    DiningTable expected = createTestDiningTable();

    // When
    when(diningTableRepository.save(any(DiningTable.class))).thenReturn(expected);

    // Then
    DiningTable saved = diningTableService.create(input);

    assertThat(saved).isNotNull();
    assertThat(saved.getCapacity()).isEqualTo(expected.getCapacity());
    assertThat(saved.getStatus()).isEqualTo(expected.getStatus());
    assertThat(saved.getRestaurant()).isEqualTo(expected.getRestaurant());

    verify(restaurantService).validateExists(input.getRestaurant().getId());
    verify(diningTableRepository).save(any(DiningTable.class));

  }

  @Test
  void create_should_throw_exception_when_restaurant_not_exists() {
    // Given
    DiningTable input = createTestDiningTable();

    // When
    doThrow(new EntityNotFoundException(
        "Restaurant not found with id : " + input.getRestaurant().getId()))
        .when(restaurantService).validateExists(input.getRestaurant().getId());

    // Then
    assertThatThrownBy(() -> diningTableService.create(input))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Restaurant not found with id : " + input.getRestaurant().getId());

    verify(restaurantService).validateExists(input.getRestaurant().getId());
    verify(diningTableRepository, never()).save(any(DiningTable.class));
  }

  @Test
  void create_should_throw_exception_when_capacity_below_min() {
    // Given
    Restaurant restaurant = createTestRestaurant();
    DiningTable input = createTestDiningTable(restaurant, 1, DiningTableStatus.AVAILABLE);

    // When & Then
    assertThatThrownBy(() -> diningTableService.create(input))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La capacité doit être entre 2 et 8 personnes");
  }

  @Test
  void create_should_throw_exception_when_capacity_above_max() {
    // Given
    Restaurant restaurant = createTestRestaurant();
    DiningTable input = createTestDiningTable(restaurant, 10, DiningTableStatus.AVAILABLE);

    // When & Then
    assertThatThrownBy(() -> diningTableService.create(input))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La capacité doit être entre 2 et 8 personnes");
  }

  @Test
  void update_should_verify_existence_and_save_dining_table() {
    //Given
    Restaurant restaurant = createTestRestaurantWithId(1L);
    DiningTable diningTable = createTestDiningTableWithId(1L, restaurant, 4,
        DiningTableStatus.AVAILABLE);
    DiningTable updateTable = createTestDiningTableWithId(1L, restaurant, 8,
        DiningTableStatus.MAINTENANCE);

    //When
    when(diningTableRepository.findById(1L)).thenReturn(Optional.of(diningTable));
    when(diningTableRepository.save(any(DiningTable.class))).thenReturn(updateTable);

    //Then
    DiningTable saved = diningTableService.update(1L, updateTable);

    assertThat(saved).isNotNull();
    assertThat(saved.getId()).isEqualTo(updateTable.getId());
    assertThat(saved.getRestaurant()).isEqualTo(updateTable.getRestaurant());
    assertThat(saved.getCapacity()).isEqualTo(updateTable.getCapacity());
    assertThat(saved.getStatus()).isEqualTo(updateTable.getStatus());

    verify(restaurantService).validateExists(1L);
    verify(diningTableRepository).save(any(DiningTable.class));
  }

  @Test
  void update_should_throw_exception_when_not_exists() {
    //Given
    Long nonExistentId = 999L;
    DiningTable diningTable = createTestDiningTable();
    //When
    when(diningTableRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> diningTableService.update(nonExistentId, diningTable))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Dining table not found with id : " + nonExistentId);
    verify(diningTableRepository).findById(nonExistentId);
    verify(diningTableRepository, never()).save(any(DiningTable.class));
  }

  @Test
  void deleteById_should_verify_existence_and_delete() {
    //Given
    Restaurant restaurant = createTestRestaurantWithId(1L);
    DiningTable input = createTestDiningTableWithId(1L, restaurant, 4, DiningTableStatus.AVAILABLE);

    //When
    when(diningTableRepository.findById(1L)).thenReturn(Optional.of(input));

    //Then
    diningTableService.deleteById(1L);

    verify(diningTableRepository).findById(1L);
    verify(diningTableRepository).deleteById(1L);
  }

  @Test
  void deleteById_should_throw_exception_when_not_exists() {
    //Given
    Long nonExistentId = 999L;
    //When
    when(diningTableRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    //Then
    assertThatThrownBy(() -> diningTableService.deleteById(nonExistentId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Dining table not found with id : " + nonExistentId);

    verify(diningTableRepository).findById(nonExistentId);
    verify(diningTableRepository, never()).deleteById(nonExistentId);
  }

}