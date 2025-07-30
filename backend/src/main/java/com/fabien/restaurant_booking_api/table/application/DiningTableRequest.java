package com.fabien.restaurant_booking_api.table.application;

import com.fabien.restaurant_booking_api.table.domain.DiningTableStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiningTableRequest {

  @NotNull(message = "L'identifiant du restaurant est obligatoire")
  private Long restaurantId;

  @NotNull(message = "La capacité est obligatoire")
  private Integer capacity;

  @NotNull(message = "Le statut est obligatoire")
  private DiningTableStatus status;
}
