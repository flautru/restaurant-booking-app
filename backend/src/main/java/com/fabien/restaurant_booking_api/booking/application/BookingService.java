package com.fabien.restaurant_booking_api.booking.application;

import com.fabien.restaurant_booking_api.booking.domain.Booking;
import com.fabien.restaurant_booking_api.booking.domain.BookingRepository;
import com.fabien.restaurant_booking_api.customer.application.CustomerService;
import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.table.application.DiningTableService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final CustomerService customerService;
  private final DiningTableService diningTableService;

  public List<Booking> findAll() {
    return bookingRepository.findAll();
  }

  public Booking findById(Long id) {
    return bookingRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Booking not found with id : " + id));
  }

  public Booking create(Booking booking) {

    Customer customer = handleCustomer(booking.getCustomer());
    booking.setCustomer(customer);

    diningTableService.validateExists(booking.getDiningTable().getId());

    validateBookingDate(booking.getDate());
    validateTableAvailability(booking);

    return bookingRepository.save(booking);
  }

  public Booking update(Long id, Booking booking) {
    findById(id);

    Customer customer = handleCustomer(booking.getCustomer());
    booking.setCustomer(customer);

    diningTableService.validateExists(booking.getDiningTable().getId());
    validateBookingDate(booking.getDate());

    validateTableAvailabilityForUpdate(id, booking);

    booking.setId(id);
    return bookingRepository.save(booking);

  }

  public void deleteById(Long id) {
    findById(id);
    bookingRepository.deleteById(id);
  }

  private Customer handleCustomer(Customer customer) {
    if (customer.getId() != null) {
      return customerService.findById(customer.getId());
    }

    return customerService.create(customer);
  }

  private void validateBookingDate(LocalDate bookingDate) {
    LocalDate today = LocalDate.now();
    LocalDate maxDate = today.plusDays(30);

    if (bookingDate.isBefore(today)) {
      throw new IllegalArgumentException("La date de réservation ne peut pas être dans le passé");
    }

    if (bookingDate.isAfter(maxDate)) {
      throw new IllegalArgumentException(
          "Les réservations ne sont possibles que 30 jours à l'avance maximum");
    }
  }

  //TODO : ACTUELLEMENT NE PREND PAS EN COMPTE LE STATUT DE LA RESERVATION (SI RESA CANCELED IMPOSSIBLE DE QUAND MEME RESA
  private void validateTableAvailability(Booking booking) {
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotType(
        booking.getDiningTable().getId(),
        booking.getDate(),
        booking.getTimeSlotType()
    );

    if (exists) {
      throw new IllegalArgumentException("Cette table est déjà réservée pour ce créneau");
    }
  }

  private void validateTableAvailabilityForUpdate(Long bookingId, Booking booking) {
    boolean exists = bookingRepository.existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot(
        booking.getDiningTable().getId(),
        booking.getDate(),
        booking.getTimeSlotType(),
        bookingId
    );

    if (exists) {
      throw new IllegalArgumentException("Cette table est déjà réservée pour ce créneau");
    }
  }
}
