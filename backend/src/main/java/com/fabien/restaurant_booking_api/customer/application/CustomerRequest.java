package com.fabien.restaurant_booking_api.customer.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {

  @NotBlank(message = "Le numéro de téléphone est obligatoire")
  private String phoneNumber;

  private String email;

  @NotBlank(message = "Le nom est obligatoire")
  private String name;
}
