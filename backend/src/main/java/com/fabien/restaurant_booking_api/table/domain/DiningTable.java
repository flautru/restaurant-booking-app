package com.fabien.restaurant_booking_api.table.domain;

import com.fabien.restaurant_booking_api.restaurant.domain.Restaurant;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dining_tables")
@Data
public class DiningTable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer capacity;

  @Enumerated(EnumType.STRING)
  private DiningTableStatus status;

  @ManyToOne
  @JoinColumn(name = "restaurant_id")
  private Restaurant restaurant;
}
