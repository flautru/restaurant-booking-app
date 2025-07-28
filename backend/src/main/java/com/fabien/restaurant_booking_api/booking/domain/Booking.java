package com.fabien.restaurant_booking_api.booking.domain;

import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "bookings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"dining_table_id", "date",
        "time_slot_type"}
    ))
@Data
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "dining_table_id")
  private DiningTable diningTable;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @Enumerated(EnumType.STRING)
  private TimeSlotType timeSlotType;

  private LocalDate date;

  private String status;
}
