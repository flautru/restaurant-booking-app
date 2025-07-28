package com.fabien.restaurant_booking_api.booking.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BookingRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private BookingRepository bookingRepository;

  @Test
  void save_should_generate_id_and_persist_booking() {
    //Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    Booking booking = createTestBooking(LocalDate.of(2024, 11, 14), TimeSlotType.DINNER_19H21H,
        "CONFIRMED",
        customer, table);

    //When
    Booking saved = bookingRepository.save(booking);

    //Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getDate()).isEqualTo(LocalDate.of(2024, 11, 14));
    assertThat(saved.getTimeSlotType()).isEqualTo(TimeSlotType.DINNER_19H21H);
    assertThat(saved.getStatus()).isEqualTo("CONFIRMED");

  }

  @Test
  void findById_should_return_booking_when_exists() {
    //Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    Booking saved = createAndPersitBooking(customer, table);

    //When
    Optional<Booking> found = bookingRepository.findById(saved.getId());

    //Then
    assertThat(found).isPresent();
    assertThat(found.get().getDate()).isEqualTo(LocalDate.of(2025, 7, 28));
    assertThat(found.get().getStatus()).isEqualTo("CONFIRMED");
    assertThat(found.get().getTimeSlotType()).isEqualTo(TimeSlotType.LUNCH_12H14H);

  }

  @Test
  void findById_should_return_empty_when_booking_not_exists() {
    // Given
    Long nonExistentId = 999L;

    // When
    Optional<Booking> found = bookingRepository.findById(nonExistentId);

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  void save_should_persist_booking_with_dining_table_and_customer_relations() {
    //Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    Booking booking = createTestBooking(LocalDate.of(2025, 7, 20), TimeSlotType.DINNER_21H23H,
        "PENDING", customer, table);

    Booking saved = bookingRepository.save(booking);

    // Then
    assertThat(saved.getDiningTable()).isNotNull();
    assertThat(saved.getDiningTable().getId()).isEqualTo(table.getId());
    assertThat(saved.getDiningTable().getCapacity()).isEqualTo(4);
    assertThat(saved.getDiningTable().getStatus()).isEqualTo("AVAILABLE");

    assertThat(saved.getCustomer()).isNotNull();
    assertThat(saved.getCustomer().getId()).isEqualTo(customer.getId());
    assertThat(saved.getCustomer().getName()).isEqualTo(customer.getName());
    assertThat(saved.getCustomer().getEmail()).isEqualTo(customer.getEmail());
    assertThat(saved.getCustomer().getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
  }

  @Test
  void save_should_throw_exception_when_duplicate_booking_same_table_date_timeslot() {
    //Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    Booking firstBooking = createAndPersitBooking(customer, table);

    Booking duplicateBooking = createTestBooking(customer, table);

    // When & Then
    assertThatThrownBy(() -> {
      bookingRepository.save(duplicateBooking);
      entityManager.flush();
    })
        .isInstanceOf(DataIntegrityViolationException.class);

  }

  @Test
  void save_should_persist_time_slot_type_as_string() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    Booking booking = createTestBooking(LocalDate.now(), TimeSlotType.LUNCH_14H16H, "CONFIRMED",
        customer, table);

    // When
    Booking saved = bookingRepository.save(booking);

    // Then
    assertThat(saved.getTimeSlotType()).isEqualTo(TimeSlotType.LUNCH_14H16H);
  }

  private Booking createTestBooking(Customer customer, DiningTable table) {
    Booking booking = new Booking();
    booking.setCustomer(customer);
    booking.setDate(LocalDate.of(2025, 7, 28));
    booking.setDiningTable(table);
    booking.setTimeSlotType(TimeSlotType.LUNCH_12H14H);
    booking.setStatus("CONFIRMED");

    return booking;
  }

  private Booking createAndPersitBooking(Customer customer, DiningTable table) {
    Booking booking = createTestBooking(customer, table);
    return entityManager.persistAndFlush(booking);
  }

  private Booking createTestBooking(LocalDate date, TimeSlotType timeSlotType, String status,
      Customer customer, DiningTable table) {

    Booking booking = new Booking();
    booking.setCustomer(customer);
    booking.setDate(date);
    booking.setDiningTable(table);
    booking.setTimeSlotType(timeSlotType);
    booking.setStatus(status);

    return booking;
  }

  private Customer createTestCustomer() {
    Customer customer = new Customer();
    customer.setName("Testons");
    customer.setEmail("test@test.com");
    customer.setPhoneNumber("99-99-99-99-99");
    return customer;
  }

  private DiningTable createTestDiningTable() {
    Restaurant restaurant = createAndPersistRestaurant();
    DiningTable diningTable = new DiningTable();
    diningTable.setRestaurant(restaurant);
    diningTable.setCapacity(4);
    diningTable.setStatus("AVAILABLE");
    return diningTable;
  }

  private Restaurant createTestRestaurant() {
    Restaurant restaurant = new Restaurant();
    restaurant.setName("Test Restaurant");
    restaurant.setAddress("123 Test St");
    restaurant.setPhoneNumber("99-99-99-99-99");
    return restaurant;
  }

  private Restaurant createAndPersistRestaurant() {
    Restaurant restaurant = createTestRestaurant();
    return entityManager.persistAndFlush(restaurant);
  }

  private DiningTable createAndPersistDiningTable() {
    DiningTable diningTable = createTestDiningTable();
    return entityManager.persistAndFlush(diningTable);
  }

  private Customer createAndPersistCustomer() {
    Customer customer = createTestCustomer();
    return entityManager.persistAndFlush(customer);
  }
}