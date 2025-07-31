package com.fabien.restaurant_booking_api.shared.utils;

import com.fabien.restaurant_booking_api.booking.domain.Booking;
import com.fabien.restaurant_booking_api.booking.domain.TimeSlotType;
import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;
import java.time.LocalDate;

public class TestDataBuilder {

  // ===== RESTAURANT BUILDERS =====

  public static Restaurant createTestRestaurant(String name, String address, String phoneNumber) {
    Restaurant restaurant = new Restaurant();
    restaurant.setName(name);
    restaurant.setAddress(address);
    restaurant.setPhoneNumber(phoneNumber);
    return restaurant;
  }

  public static Restaurant createTestRestaurantWithId(Long id, String name, String address,
      String phoneNumber) {
    Restaurant restaurant = createTestRestaurant(name, address, phoneNumber);
    restaurant.setId(id);
    return restaurant;
  }

  public static Restaurant createTestRestaurant() {
    return createTestRestaurant("Test Restaurant", "123 Test St", "555-1234");
  }

  public static Restaurant createTestRestaurantWithId(Long id) {
    return createTestRestaurantWithId(id, "Test Restaurant", "123 Test St", "555-1234");
  }

  // ===== DINING TABLE BUILDERS =====

  public static DiningTable createTestDiningTable(Restaurant restaurant, Integer capacity,
      DiningTableStatus status) {
    DiningTable table = new DiningTable();
    table.setRestaurant(restaurant);
    table.setCapacity(capacity);
    table.setStatus(status);
    return table;
  }

  public static DiningTable createTestDiningTableWithId(Long id, Restaurant restaurant,
      Integer capacity, DiningTableStatus status) {
    DiningTable table = createTestDiningTable(restaurant, capacity, status);
    table.setId(id);
    return table;
  }

  public static DiningTable createTestDiningTable(Restaurant restaurant) {
    return createTestDiningTable(restaurant, 4, DiningTableStatus.AVAILABLE);
  }

  public static DiningTable createTestDiningTable() {
    Restaurant restaurant = createTestRestaurant();
    return createTestDiningTable(restaurant, 4, DiningTableStatus.AVAILABLE);
  }

  // ===== CUSTOMER BUILDERS (pour plus tard) =====

  public static Customer createTestCustomer(String name, String email, String phoneNumber) {
    Customer customer = new Customer();
    customer.setName(name);
    customer.setEmail(email);
    customer.setPhoneNumber(phoneNumber);
    return customer;
  }

  public static Customer createTestCustomerWithId(Long id, String name, String email,
      String phoneNumber) {
    Customer customer = createTestCustomer(name, email, phoneNumber);
    customer.setId(id);
    return customer;
  }

  public static Customer createTestCustomerWithId(Long id) {
    Customer customer = createTestCustomer();
    customer.setId(id);
    return customer;
  }

  public static Customer createTestCustomer() {
    return createTestCustomer("Test Customer", "test@test.com", "99-99-99-99-99");
  }

  // ===== BOOKING BUILDERS (pour plus tard) =====

  public static Booking createTestBooking(DiningTable table, Customer customer, LocalDate date,
      TimeSlotType timeSlot) {
    Booking booking = new Booking();
    booking.setDiningTable(table);
    booking.setCustomer(customer);
    booking.setDate(date);
    booking.setTimeSlotType(timeSlot);
    booking.setStatus("CONFIRMED");
    return booking;
  }

  public static Booking createTestBooking() {
    DiningTable table = createTestDiningTable();
    Customer customer = createTestCustomer();
    return createTestBooking(table, customer, LocalDate.now(), TimeSlotType.LUNCH_12H14H);
  }
}