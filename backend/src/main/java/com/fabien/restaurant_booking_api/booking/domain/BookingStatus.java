package com.fabien.restaurant_booking_api.booking.domain;

public enum BookingStatus {
  IN_PROGRESS("En cours"),
  CANCELED("Annulée"),
  FINISH("Terminéé");

  private final String displayName;

  BookingStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
