package com.fabien.restaurant_booking_api.table.domain;

public enum DiningTableStatus {
  AVAILABLE("Disponible"),
  MAINTENANCE("En maintenance");

  private final String displayName;

  DiningTableStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
