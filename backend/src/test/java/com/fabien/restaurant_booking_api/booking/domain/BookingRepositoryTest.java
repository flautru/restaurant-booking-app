package com.fabien.restaurant_booking_api.booking.domain;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestBooking;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomer;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestDiningTable;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestRestaurant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private BookingRepository bookingRepository;

  @Test
  void save_should_generate_id_and_persist_booking() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    Booking booking = createTestBooking(table, customer, LocalDate.of(2025, 8, 15),
        TimeSlotType.DINNER_19H21H);

    // When
    Booking saved = bookingRepository.save(booking);

    // Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getDate()).isEqualTo(LocalDate.of(2025, 8, 15));
    assertThat(saved.getTimeSlotType()).isEqualTo(TimeSlotType.DINNER_19H21H);
    assertThat(saved.getStatus()).isEqualTo(BookingStatus.FINISH);
  }

  @Test
  void findById_should_return_booking_when_exists() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    Booking booking = createTestBooking(table, customer, LocalDate.of(2025, 8, 20),
        TimeSlotType.LUNCH_12H14H);
    Booking saved = entityManager.persistAndFlush(booking);

    // When
    Optional<Booking> found = bookingRepository.findById(saved.getId());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getDate()).isEqualTo(LocalDate.of(2025, 8, 20));
    assertThat(found.get().getTimeSlotType()).isEqualTo(TimeSlotType.LUNCH_12H14H);
    assertThat(found.get().getStatus()).isEqualTo(BookingStatus.FINISH);
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
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    Booking booking = createTestBooking(table, customer, LocalDate.of(2025, 8, 25),
        TimeSlotType.DINNER_21H23H);

    // When
    Booking saved = bookingRepository.save(booking);

    // Then
    assertThat(saved.getDiningTable()).isNotNull();
    assertThat(saved.getDiningTable().getId()).isEqualTo(table.getId());
    assertThat(saved.getDiningTable().getCapacity()).isEqualTo(4);
    assertThat(saved.getDiningTable().getStatus()).isEqualTo(DiningTableStatus.AVAILABLE);

    assertThat(saved.getCustomer()).isNotNull();
    assertThat(saved.getCustomer().getId()).isEqualTo(customer.getId());
    assertThat(saved.getCustomer().getName()).isEqualTo("Test Customer");
    assertThat(saved.getCustomer().getPhoneNumber()).isEqualTo("99-99-99-99-99");
  }

  @Test
  void save_should_throw_exception_when_duplicate_booking_same_table_date_timeslot() {
    // Given
    Customer customer1 = createAndPersistCustomer();
    Customer customer2 = createAndPersistCustomer("MarieTest", "test@test.com", "00-11-22-33-44");
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.of(2025, 8, 30);
    TimeSlotType timeSlot = TimeSlotType.LUNCH_14H16H;

    Booking booking1 = createTestBooking(table, customer1, bookingDate, timeSlot);
    entityManager.persistAndFlush(booking1);

    Booking booking2 = createTestBooking(table, customer2, bookingDate, timeSlot);

    // When & Then
    assertThatThrownBy(() -> {
      bookingRepository.save(booking2);
      entityManager.flush();
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void existsByDiningTableIdAndDateAndTimeSlotType_should_return_true_when_booking_exists() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.of(2025, 9, 5);
    TimeSlotType timeSlot = TimeSlotType.DINNER_19H21H;

    Booking existingBooking = createTestBooking(table, customer, bookingDate, timeSlot);
    entityManager.persistAndFlush(existingBooking);

    // When
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(
        table.getId(), bookingDate, timeSlot);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  void existsByDiningTableIdAndDateAndTimeSlotType_should_return_false_when_no_booking() {
    // Given
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.of(2025, 9, 10);
    TimeSlotType timeSlot = TimeSlotType.LUNCH_12H14H;

    // When
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(
        table.getId(), bookingDate, timeSlot);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void existsByDiningTableIdAndDateAndTimeSlotType_should_return_false_when_different_table() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table1 = createAndPersistDiningTable();
    DiningTable table2 = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.of(2025, 9, 15);
    TimeSlotType timeSlot = TimeSlotType.DINNER_21H23H;

    Booking booking = createTestBooking(table1, customer, bookingDate, timeSlot);
    entityManager.persistAndFlush(booking);

    // When
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(
        table2.getId(), bookingDate, timeSlot);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void existsByDiningTableIdAndDateAndTimeSlotType_should_return_false_when_different_date() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    LocalDate existingDate = LocalDate.of(2025, 9, 20);
    LocalDate differentDate = LocalDate.of(2025, 9, 21);
    TimeSlotType timeSlot = TimeSlotType.LUNCH_14H16H;

    Booking booking = createTestBooking(table, customer, existingDate, timeSlot);
    entityManager.persistAndFlush(booking);

    // When
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(
        table.getId(), differentDate, timeSlot);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void existsByDiningTableIdAndDateAndTimeSlotType_should_return_false_when_different_time_slot() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.of(2025, 9, 25);
    TimeSlotType existingSlot = TimeSlotType.LUNCH_12H14H;
    TimeSlotType differentSlot = TimeSlotType.LUNCH_14H16H;

    Booking booking = createTestBooking(table, customer, bookingDate, existingSlot);
    entityManager.persistAndFlush(booking);

    // When
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(
        table.getId(), bookingDate, differentSlot);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot_should_return_false_when_same_booking() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.of(2025, 10, 1);
    TimeSlotType timeSlot = TimeSlotType.DINNER_19H21H;

    Booking existingBooking = createTestBooking(table, customer, bookingDate, timeSlot);
    Booking saved = entityManager.persistAndFlush(existingBooking);

    // When
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot(
        table.getId(), bookingDate, timeSlot, saved.getId());

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot_should_return_true_when_other_booking_exists() {
    // Given
    Customer customer1 = createAndPersistCustomer();
    Customer customer2 = createAndPersistCustomer("MarieTest", "test@test.com", "00-11-22-33-44");
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.of(2025, 10, 5);
    TimeSlotType timeSlot = TimeSlotType.DINNER_21H23H;

    Booking booking1 = createTestBooking(table, customer1, bookingDate, timeSlot);
    Booking saved1 = entityManager.persistAndFlush(booking1);

    Booking booking2 = createTestBooking(table, customer2, bookingDate, TimeSlotType.DINNER_19H21H);
    Booking saved2 = entityManager.persistAndFlush(booking2);

    // When
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot(
        table.getId(), bookingDate, TimeSlotType.DINNER_21H23H, saved2.getId());

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  void existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot_should_return_false_when_no_other_booking() {
    // Given
    Customer customer = createAndPersistCustomer();
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.of(2025, 10, 10);
    TimeSlotType timeSlot = TimeSlotType.LUNCH_12H14H;

    Booking booking = createTestBooking(table, customer, bookingDate, timeSlot);
    Booking saved = entityManager.persistAndFlush(booking);

    // When
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot(
        table.getId(), bookingDate, timeSlot, saved.getId());

    // Then
    assertThat(exists).isFalse();
  }

  private Customer createAndPersistCustomer() {
    Customer customer = createTestCustomer();
    return entityManager.persistAndFlush(customer);
  }

  private Customer createAndPersistCustomer(String name, String email, String phoneNumber) {
    Customer customer = createTestCustomer(name, email, phoneNumber);
    return entityManager.persistAndFlush(customer);
  }

  private DiningTable createAndPersistDiningTable() {
    Restaurant restaurant = createTestRestaurant();
    Restaurant savedRestaurant = entityManager.persistAndFlush(restaurant);

    DiningTable table = createTestDiningTable(savedRestaurant);
    return entityManager.persistAndFlush(table);
  }
}