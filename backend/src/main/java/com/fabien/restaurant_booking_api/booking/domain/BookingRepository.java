package com.fabien.restaurant_booking_api.booking.domain;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  
  boolean existsByDiningTableIdAndDateAndTimeSlotType(
      Long diningTableId,
      LocalDate date,
      TimeSlotType timeSlotType
  );

  boolean existsByDiningTableIdAndDateAndTimeSlotTypeAndIdNot(
      Long diningTableId,
      LocalDate date,
      TimeSlotType timeSlotType,
      Long id
  );
}
