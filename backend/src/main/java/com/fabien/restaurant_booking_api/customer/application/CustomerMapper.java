package com.fabien.restaurant_booking_api.customer.application;

import com.fabien.restaurant_booking_api.customer.domain.Customer;

public class CustomerMapper {

  public static CustomerResponse toResponse(Customer customer) {
    if (customer == null) {
      return null;
    }
    return new CustomerResponse(
        customer.getId(),
        customer.getPhoneNumber(),
        customer.getEmail(),
        customer.getName()
    );
  }

  public static Customer toEntity(CustomerRequest request) {
    if (request == null) {
      return null;
    }

    Customer customer = new Customer();
    customer.setName(request.getName());
    customer.setEmail(request.getEmail());
    customer.setPhoneNumber(request.getPhoneNumber());

    return customer;
  }
}
