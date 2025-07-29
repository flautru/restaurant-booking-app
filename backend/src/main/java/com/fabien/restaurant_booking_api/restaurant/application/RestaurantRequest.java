package com.fabien.restaurant_booking_api.restaurant.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour les opérations de création et modification de Restaurant.
 * <p>
 * Note : Actuellement partagé entre Create et Update pour simplifier le MVP. Sera séparé en
 * CreateRestaurantRequest et UpdateRestaurantRequest quand les règles métier divergeront (ex:
 * modifications partielles, validations différentes).
 * <p>
 * Évolution prévue : - Create : tous les champs obligatoires - Update : champs optionnels
 * (sémantique PATCH)
 */
@Data
public class RestaurantRequest {

  @NotBlank(message = "Le nom est obligatoire")
  private String name;

  @NotBlank(message = "L'adresse est obligatoire")
  private String address;

  @NotBlank(message = "Le numéro de téléphone est obligatoire")
  private String phoneNumber;

}
