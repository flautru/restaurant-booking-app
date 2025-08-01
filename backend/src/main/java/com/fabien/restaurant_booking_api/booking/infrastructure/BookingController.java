package com.fabien.restaurant_booking_api.booking.infrastructure;

import com.fabien.restaurant_booking_api.booking.application.BookingMapper;
import com.fabien.restaurant_booking_api.booking.application.BookingRequest;
import com.fabien.restaurant_booking_api.booking.application.BookingResponse;
import com.fabien.restaurant_booking_api.booking.application.BookingService;
import com.fabien.restaurant_booking_api.booking.domain.Booking;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;

  @GetMapping
  public ResponseEntity<List<BookingResponse>> findAll() {
    List<BookingResponse> bookingResponses = bookingService.findAll()
        .stream()
        .map(BookingMapper::toResponse)
        .toList();
    return ResponseEntity.ok(bookingResponses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookingResponse> findById(@PathVariable Long id) {
    Booking booking = bookingService.findById(id);
    return ResponseEntity.ok(BookingMapper.toResponse(booking));
  }

  @PostMapping
  public ResponseEntity<BookingResponse> create(
      @Valid @RequestBody BookingRequest request) {
    Booking booking = BookingMapper.toEntity(request);

    BookingResponse response = BookingMapper.toResponse(bookingService.create(booking));
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.id())
        .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookingResponse> update(@PathVariable Long id,
      @Valid @RequestBody BookingRequest request) {
    Booking updatedBooking = bookingService.update(id, BookingMapper.toEntity(request));
    return ResponseEntity.ok(BookingMapper.toResponse(updatedBooking));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    bookingService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
