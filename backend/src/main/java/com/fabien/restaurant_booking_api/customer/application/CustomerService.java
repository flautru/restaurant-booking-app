package com.fabien.restaurant_booking_api.customer.application;

import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.customer.domain.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  public List<Customer> findAll() {
    return customerRepository.findAll();
  }

  public Customer findById(Long id) {
    return customerRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Customer not found with id : " + id));
  }

  public Customer create(Customer customer) {
    return customerRepository.save(customer);
  }

  public Customer update(Long id, Customer customer) {
    findById(id);
    customer.setId(id);
    return customerRepository.save(customer);
  }

  public void deleteById(Long id) {
    findById(id);
    customerRepository.deleteById(id);
  }
}
