package com.fabien.restaurant_booking_api.booking.application;

import com.fabien.restaurant_booking_api.booking.domain.BookingStatus;
import com.fabien.restaurant_booking_api.booking.domain.TimeSlotType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingRequest {

  @NotNull(message = "L'identifiant de la table est obligatoire")
  private Long diningTableId;

  private Long customerId;
  private String customerName;
  private String customerEmail;
  private String customerPhoneNumber;
  
  @NotNull(message = "Le créneau horaire est obligatoire")
  private TimeSlotType timeSlotType;

  @NotNull(message = "La date est obligatoire")
  private LocalDate date;

  @NotNull(message = "Le statut est obligatoire")
  private BookingStatus status;

}