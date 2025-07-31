package com.fabien.restaurant_booking_api.booking.application;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestBooking;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestBookingWithId;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomer;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomerWithId;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestDiningTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fabien.restaurant_booking_api.booking.domain.Booking;
import com.fabien.restaurant_booking_api.booking.domain.BookingRepository;
import com.fabien.restaurant_booking_api.booking.domain.BookingStatus;
import com.fabien.restaurant_booking_api.booking.domain.TimeSlotType;
import com.fabien.restaurant_booking_api.customer.application.CustomerService;
import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.table.application.DiningTableService;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private CustomerService customerService;

  @Mock
  private DiningTableService diningTableService;

  private BookingService bookingService;

  @BeforeEach
  void setUp() {
    bookingService = new BookingService(bookingRepository, customerService, diningTableService);
  }

  @Test
  void findAll_should_return_all_bookings() {
    // Given
    DiningTable table = createTestDiningTable();
    Customer customer1 = createTestCustomerWithId(1L);
    Customer customer2 = createTestCustomerWithId(2L);

    Booking booking1 = createTestBookingWithId(1L, table, customer1, LocalDate.of(2025, 8, 15),
        TimeSlotType.LUNCH_12H14H, BookingStatus.FINISH);
    Booking booking2 = createTestBookingWithId(2L, table, customer2, LocalDate.of(2025, 8, 16),
        TimeSlotType.DINNER_19H21H, BookingStatus.CANCELED);

    when(bookingRepository.findAll()).thenReturn(List.of(booking1, booking2));

    // When
    List<Booking> results = bookingService.findAll();

    // Then
    assertThat(results).hasSize(2)
        .extracting(Booking::getId)
        .containsExactly(1L, 2L);

    assertThat(results)
        .extracting(Booking::getDate)
        .containsExactly(LocalDate.of(2025, 8, 15), LocalDate.of(2025, 8, 16));

    verify(bookingRepository).findAll();
  }

  @Test
  void findById_should_return_booking_when_exists() {
    // Given
    DiningTable table = createTestDiningTable();
    Customer customer = createTestCustomerWithId(1L);
    Booking booking = createTestBookingWithId(1L, table, customer, LocalDate.of(2025, 8, 20),
        TimeSlotType.DINNER_21H23H, BookingStatus.CANCELED);

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // When
    Booking result = bookingService.findById(1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 8, 20));
    assertThat(result.getTimeSlotType()).isEqualTo(TimeSlotType.DINNER_21H23H);
    assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELED);

    verify(bookingRepository).findById(1L);
  }

  @Test
  void findById_should_throw_exception_when_not_exists() {
    // Given
    Long nonExistentId = 999L;
    when(bookingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> bookingService.findById(nonExistentId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Booking not found with id : " + nonExistentId);

    verify(bookingRepository).findById(nonExistentId);
  }

  @Test
  void create_should_save_booking_with_existing_customer() {
    // Given
    Customer existingCustomer = createTestCustomerWithId(1L);
    DiningTable table = createTestDiningTable();
    table.setId(1L);

    LocalDate futureDate = LocalDate.now().plusDays(15);
    Booking inputBooking = createTestBooking(table, existingCustomer, futureDate,
        TimeSlotType.LUNCH_14H16H, BookingStatus.IN_PROGRESS);
    Booking expectedBooking = createTestBooking(table, existingCustomer, futureDate,
        TimeSlotType.LUNCH_14H16H, BookingStatus.IN_PROGRESS);

    when(customerService.findById(1L)).thenReturn(existingCustomer);
    when(bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(1L, futureDate,
        TimeSlotType.LUNCH_14H16H))
        .thenReturn(false);
    when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);

    // When
    Booking result = bookingService.create(inputBooking);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getCustomer()).isEqualTo(existingCustomer);
    assertThat(result.getDate()).isEqualTo(futureDate);

    verify(customerService).findById(1L);
    verify(diningTableService).validateExists(1L);
    verify(bookingRepository).existsByDiningTableIdAndDateAndTimeSlotType(1L, futureDate,
        TimeSlotType.LUNCH_14H16H);
    verify(bookingRepository).save(any(Booking.class));
  }

  @Test
  void create_should_save_booking_with_new_customer() {
    // Given
    Customer newCustomer = createTestCustomer("New Customer", "new@test.com", "01-11-11-11-11");
    Customer createdCustomer = createTestCustomerWithId(2L, "New Customer", "new@test.com",
        "01-11-11-11-11");
    DiningTable table = createTestDiningTable();
    table.setId(1L);

    LocalDate futureDate = LocalDate.now().plusDays(20);
    Booking inputBooking = createTestBooking(table, newCustomer, futureDate,
        TimeSlotType.DINNER_19H21H, BookingStatus.IN_PROGRESS);
    Booking expectedBooking = createTestBooking(table, createdCustomer, futureDate,
        TimeSlotType.DINNER_19H21H, BookingStatus.IN_PROGRESS);

    when(customerService.create(newCustomer)).thenReturn(createdCustomer);
    when(bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(1L, futureDate,
        TimeSlotType.DINNER_19H21H))
        .thenReturn(false);
    when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);

    // When
    Booking result = bookingService.create(inputBooking);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getCustomer()).isEqualTo(createdCustomer);

    verify(customerService).create(newCustomer);
    verify(customerService, never()).findById(any());
    verify(diningTableService).validateExists(1L);
    verify(bookingRepository).save(any(Booking.class));
  }

  @Test
  void create_should_throw_exception_when_date_in_past() {
    // Given
    Customer customer = createTestCustomerWithId(1L);
    DiningTable table = createTestDiningTable();
    table.setId(1L);

    LocalDate pastDate = LocalDate.now().minusDays(1);
    Booking booking = createTestBooking(table, customer, pastDate, TimeSlotType.LUNCH_12H14H,
        BookingStatus.IN_PROGRESS);

    when(customerService.findById(1L)).thenReturn(customer);

    // When & Then
    assertThatThrownBy(() -> bookingService.create(booking))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La date de réservation ne peut pas être dans le passé");

    verify(customerService).findById(1L);
    verify(diningTableService).validateExists(1L);
    verify(bookingRepository, never()).existsByDiningTableIdAndDateAndTimeSlotType(any(), any(),
        any());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  void create_should_throw_exception_when_date_too_far() {
    // Given
    Customer customer = createTestCustomerWithId(1L);
    DiningTable table = createTestDiningTable();
    table.setId(1L);

    LocalDate tooFarDate = LocalDate.now().plusDays(31);
    Booking booking = createTestBooking(table, customer, tooFarDate, TimeSlotType.DINNER_21H23H,
        BookingStatus.IN_PROGRESS);

    when(customerService.findById(1L)).thenReturn(customer);

    // When & Then
    assertThatThrownBy(() -> bookingService.create(booking))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Les réservations ne sont possibles que 30 jours à l'avance maximum");

    verify(diningTableService).validateExists(1L);
    verify(bookingRepository, never()).save(any());
  }

  @Test
  void create_should_throw_exception_when_table_already_booked() {
    // Given
    Customer customer = createTestCustomerWithId(1L);
    DiningTable table = createTestDiningTable();
    table.setId(1L);

    LocalDate futureDate = LocalDate.now().plusDays(10);
    TimeSlotType timeSlot = TimeSlotType.LUNCH_12H14H;
    Booking booking = createTestBooking(table, customer, futureDate, timeSlot,
        BookingStatus.IN_PROGRESS);

    when(customerService.findById(1L)).thenReturn(customer);
    when(bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(1L, futureDate, timeSlot))
        .thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> bookingService.create(booking))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Cette table est déjà réservée pour ce créneau");

    verify(diningTableService).validateExists(1L);
    verify(bookingRepository).existsByDiningTableIdAndDateAndTimeSlotType(1L, futureDate, timeSlot);
    verify(bookingRepository, never()).save(any());
  }

  @Test
  void update_should_update_booking_successfully() {
    // Given
    Customer existingCustomer = createTestCustomerWithId(1L);
    DiningTable table = createTestDiningTable();
    table.setId(1L);

    LocalDate newDate = LocalDate.now().plusDays(25);
    Booking existingBooking = createTestBookingWithId(1L, table, existingCustomer,
        LocalDate.now().plusDays(15), TimeSlotType.LUNCH_12H14H, BookingStatus.IN_PROGRESS);
    Booking updateBooking = createTestBooking(table, existingCustomer, newDate,
        TimeSlotType.DINNER_19H21H, BookingStatus.IN_PROGRESS);
    Booking expectedBooking = createTestBookingWithId(1L, table, existingCustomer, newDate,
        TimeSlotType.DINNER_19H21H, BookingStatus.IN_PROGRESS);

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
    when(customerService.findById(1L)).thenReturn(existingCustomer);
    when(bookingRepository.existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot(1L, newDate,
        TimeSlotType.DINNER_19H21H, 1L))
        .thenReturn(false);
    when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);

    // When
    Booking result = bookingService.update(1L, updateBooking);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getDate()).isEqualTo(newDate);
    assertThat(result.getTimeSlotType()).isEqualTo(TimeSlotType.DINNER_19H21H);
    assertThat(result.getStatus()).isEqualTo(BookingStatus.IN_PROGRESS);

    verify(bookingRepository).findById(1L);
    verify(customerService).findById(1L);
    verify(diningTableService).validateExists(1L);
    verify(bookingRepository).existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot(1L, newDate,
        TimeSlotType.DINNER_19H21H, 1L);
    verify(bookingRepository).save(any(Booking.class));
  }

  @Test
  void deleteById_should_verify_existence_and_delete() {
    // Given
    Customer customer = createTestCustomerWithId(1L);
    DiningTable table = createTestDiningTable();
    Booking booking = createTestBookingWithId(1L, table, customer, LocalDate.of(2025, 8, 25),
        TimeSlotType.LUNCH_14H16H, BookingStatus.IN_PROGRESS);

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // When
    bookingService.deleteById(1L);

    // Then
    verify(bookingRepository).findById(1L);
    verify(bookingRepository).deleteById(1L);
  }

  @Test
  void deleteById_should_throw_exception_when_not_exists() {
    // Given
    Long nonExistentId = 999L;
    when(bookingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> bookingService.deleteById(nonExistentId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Booking not found with id : " + nonExistentId);

    verify(bookingRepository).findById(nonExistentId);
    verify(bookingRepository, never()).deleteById(any());
  }
}