package com.fabien.restaurant_booking_api.booking.infrastructure;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestBooking;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomer;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestDiningTable;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestRestaurant;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fabien.restaurant_booking_api.booking.domain.Booking;
import com.fabien.restaurant_booking_api.booking.domain.BookingRepository;
import com.fabien.restaurant_booking_api.booking.domain.BookingStatus;
import com.fabien.restaurant_booking_api.booking.domain.TimeSlotType;
import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.customer.domain.CustomerRepository;
import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.restaurant.domain.RestaurantRepository;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import com.fabien.restaurant_booking_api.table.domain.DiningTableRepository;
import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests d'intégration pour BookingController.
 * <p>
 * Basé sur les vraies classes BookingRequest/BookingResponse du projet Format JSON : diningTableId,
 * customerId, timeSlotType, date, status
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private DiningTableRepository diningTableRepository;

  @Autowired
  private RestaurantRepository restaurantRepository;

  @AfterEach
  void tearDown() {
    bookingRepository.deleteAll();
    customerRepository.deleteAll();
    diningTableRepository.deleteAll();
    restaurantRepository.deleteAll();
  }

  @Test
  void findAll_should_return_all_bookings_with_200() throws Exception {
    // Given
    Customer customer1 = createAndPersistCustomer("Jean Test", "jean@test.com", "01-11-11-11-11");
    Customer customer2 = createAndPersistCustomer("Marie Test", "marie@test.com", "01-22-22-22-22");
    DiningTable table = createAndPersistDiningTable();

    LocalDate date1 = LocalDate.now().plusDays(10);
    LocalDate date2 = LocalDate.now().plusDays(15);

    Booking booking1 = createTestBooking(table, customer1, date1, TimeSlotType.LUNCH_12H14H,
        BookingStatus.IN_PROGRESS);
    Booking booking2 = createTestBooking(table, customer2, date2, TimeSlotType.DINNER_19H21H,
        BookingStatus.IN_PROGRESS);

    bookingRepository.save(booking1);
    bookingRepository.save(booking2);

    // When & Then
    mockMvc.perform(get("/api/bookings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].date", is(date1.format(DateTimeFormatter.ISO_LOCAL_DATE))))
        .andExpect(jsonPath("$[0].timeSlotType", is("LUNCH_12H14H")))
        .andExpect(jsonPath("$[0].status", is("IN_PROGRESS")))
        .andExpect(jsonPath("$[0].customer.name", is("Jean Test")))
        .andExpect(jsonPath("$[0].table.capacity", is(4)))
        .andExpect(jsonPath("$[1].date", is(date2.format(DateTimeFormatter.ISO_LOCAL_DATE))))
        .andExpect(jsonPath("$[1].timeSlotType", is("DINNER_19H21H")));
  }

  @Test
  void findAll_should_return_empty_list_when_no_bookings() throws Exception {
    // When & Then
    mockMvc.perform(get("/api/bookings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void findById_should_return_booking_with_200_when_exists() throws Exception {
    // Given
    Customer customer = createAndPersistCustomer("Test Customer", "test@test.com",
        "01-23-45-67-89");
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.now().plusDays(20);

    Booking booking = createTestBooking(table, customer, bookingDate, TimeSlotType.DINNER_21H23H,
        BookingStatus.IN_PROGRESS);
    Booking saved = bookingRepository.save(booking);

    // When & Then
    mockMvc.perform(get("/api/bookings/{id}", saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
        .andExpect(
            jsonPath("$.date", is(bookingDate.format(DateTimeFormatter.ISO_LOCAL_DATE))))
        .andExpect(jsonPath("$.timeSlotType", is("DINNER_21H23H")))
        .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
        .andExpect(jsonPath("$.customer.name", is("Test Customer")))
        .andExpect(jsonPath("$.customer.phoneNumber", is("01-23-45-67-89")))
        .andExpect(jsonPath("$.table.capacity", is(4)));
  }

  @Test
  void findById_should_return_404_when_booking_not_exists() throws Exception {
    // Given
    Long nonExistentId = 999L;

    // When & Then
    mockMvc.perform(get("/api/bookings/{id}", nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_should_create_booking_with_existing_customer_and_return_201() throws Exception {
    // Given
    Customer existingCustomer = createAndPersistCustomer("Existing Customer", "existing@test.com",
        "01-33-33-33-33");
    DiningTable table = createAndPersistDiningTable();
    LocalDate futureDate = LocalDate.now().plusDays(25);

    String requestJson = String.format("""
            {
              "diningTableId": %d,
              "customerId": %d,
              "timeSlotType": "LUNCH_14H16H",
              "date": "%s",
              "status": "IN_PROGRESS"
            }
            """, table.getId(), existingCustomer.getId(),
        futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When & Then
    mockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", matchesPattern(".*/api/bookings/\\d+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.date", is(futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE))))
        .andExpect(jsonPath("$.timeSlotType", is("LUNCH_14H16H")))
        .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
        .andExpect(jsonPath("$.customer.id", is(existingCustomer.getId().intValue())))
        .andExpect(jsonPath("$.customer.name", is("Existing Customer")));
  }

  @Test
  void create_should_create_booking_with_new_customer_and_return_201() throws Exception {
    // Given
    DiningTable table = createAndPersistDiningTable();
    LocalDate futureDate = LocalDate.now().plusDays(12);

    String requestJson = String.format("""
        {
          "diningTableId": %d,
          "customerId": null,
          "customerName": "Nouveau Client",
          "customerEmail": "nouveau@test.com",
          "customerPhoneNumber": "01-99-88-77-66",
          "timeSlotType": "DINNER_19H21H",
          "date": "%s",
          "status": "IN_PROGRESS"
        }
        """, table.getId(), futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When & Then
    mockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.customer.id").exists())
        .andExpect(jsonPath("$.customer.name", is("Nouveau Client")))
        .andExpect(jsonPath("$.customer.email", is("nouveau@test.com")))
        .andExpect(jsonPath("$.customer.phoneNumber", is("01-99-88-77-66")));
  }

  @Test
  void create_should_return_400_when_date_in_past() throws Exception {
    // Given
    Customer customer = createAndPersistCustomer("Test Customer", "test@test.com",
        "01-11-11-11-11");
    DiningTable table = createAndPersistDiningTable();
    LocalDate pastDate = LocalDate.now().minusDays(1);

    String requestJson = String.format("""
        {
          "diningTableId": %d,
          "customerId": %d,
          "timeSlotType": "LUNCH_12H14H",
          "date": "%s",
          "status": "IN_PROGRESS"
        }
        """, table.getId(), customer.getId(), pastDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When & Then
    mockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error", is("La date de réservation ne peut pas être dans le passé")));
  }

  @Test
  void create_should_return_400_when_date_too_far() throws Exception {
    // Given
    Customer customer = createAndPersistCustomer("Test Customer", "test@test.com",
        "01-11-11-11-11");
    DiningTable table = createAndPersistDiningTable();
    LocalDate tooFarDate = LocalDate.now().plusDays(35); // > 30 jours

    String requestJson = String.format("""
        {
          "diningTableId": %d,
          "customerId": %d,
          "timeSlotType": "DINNER_21H23H", 
          "date": "%s",
          "status": "IN_PROGRESS"
        }
        """, table.getId(), customer.getId(), tooFarDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When & Then
    mockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error",
            is("Les réservations ne sont possibles que 30 jours à l'avance maximum")));
  }

  @Test
  void create_should_return_400_when_table_already_booked() throws Exception {
    // Given
    Customer customer1 = createAndPersistCustomer("Customer 1", "customer1@test.com",
        "01-11-11-11-11");
    Customer customer2 = createAndPersistCustomer("Customer 2", "customer2@test.com",
        "01-22-22-22-22");
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.now().plusDays(18);
    TimeSlotType timeSlot = TimeSlotType.LUNCH_12H14H;

    Booking existingBooking = createTestBooking(table, customer1, bookingDate, timeSlot,
        BookingStatus.IN_PROGRESS);
    bookingRepository.save(existingBooking);

    String requestJson = String.format("""
            {
              "diningTableId": %d,
              "customerId": %d,
              "timeSlotType": "LUNCH_12H14H",
              "date": "%s",
              "status": "IN_PROGRESS"
            }
            """, table.getId(), customer2.getId(),
        bookingDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When & Then
    mockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("Cette table est déjà réservée pour ce créneau")));
  }

  @Test
  void create_should_return_404_when_dining_table_not_exists() throws Exception {
    // Given
    Customer customer = createAndPersistCustomer("Test Customer", "test@test.com",
        "01-11-11-11-11");
    Long nonExistentTableId = 999L;
    LocalDate futureDate = LocalDate.now().plusDays(10);

    String requestJson = String.format("""
            {
              "diningTableId": %d,
              "customerId": %d,
              "timeSlotType": "LUNCH_14H16H",
              "date": "%s",
              "status": "IN_PROGRESS"
            }
            """, nonExistentTableId, customer.getId(),
        futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When & Then
    mockMvc.perform(post("/api/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void update_should_update_booking_and_return_200() throws Exception {
    // Given
    Customer customer = createAndPersistCustomer("Original Customer", "original@test.com",
        "01-55-55-55-55");
    DiningTable table = createAndPersistDiningTable();

    LocalDate originalDate = LocalDate.now().plusDays(10);
    LocalDate newDate = LocalDate.now().plusDays(22);

    Booking originalBooking = createTestBooking(table, customer, originalDate,
        TimeSlotType.LUNCH_12H14H, BookingStatus.IN_PROGRESS);
    Booking saved = bookingRepository.save(originalBooking);

    String updateJson = String.format("""
        {
          "diningTableId": %d,
          "customerId": %d,
          "timeSlotType": "DINNER_21H23H",
          "date": "%s",
          "status": "IN_PROGRESS"
        }
        """, table.getId(), customer.getId(), newDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

    // When & Then
    mockMvc.perform(put("/api/bookings/{id}", saved.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
        .andExpect(jsonPath("$.date", is(newDate.format(DateTimeFormatter.ISO_LOCAL_DATE))))
        .andExpect(jsonPath("$.timeSlotType", is("DINNER_21H23H")));
  }

  @Test
  void delete_should_delete_booking_and_return_204() throws Exception {
    // Given
    Customer customer = createAndPersistCustomer("To Delete", "delete@test.com", "01-99-99-99-99");
    DiningTable table = createAndPersistDiningTable();
    LocalDate bookingDate = LocalDate.now().plusDays(15);

    Booking booking = createTestBooking(table, customer, bookingDate, TimeSlotType.LUNCH_14H16H,
        BookingStatus.IN_PROGRESS);
    Booking saved = bookingRepository.save(booking);

    // When & Then
    mockMvc.perform(delete("/api/bookings/{id}", saved.getId()))
        .andExpect(status().isNoContent());
    
    mockMvc.perform(get("/api/bookings/{id}", saved.getId()))
        .andExpect(status().isNotFound());
  }

  // Helper methods
  private Customer createAndPersistCustomer(String name, String email, String phoneNumber) {
    Customer customer = createTestCustomer(name, email, phoneNumber);
    return customerRepository.save(customer);
  }

  private DiningTable createAndPersistDiningTable() {
    Restaurant restaurant = createTestRestaurant("Test Restaurant", "123 Test Street",
        "01-00-00-00-00");
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);

    DiningTable table = createTestDiningTable(savedRestaurant, 4, DiningTableStatus.AVAILABLE);
    return diningTableRepository.save(table);
  }
}