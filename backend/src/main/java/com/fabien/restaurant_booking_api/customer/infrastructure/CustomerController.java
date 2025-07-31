package com.fabien.restaurant_booking_api.customer.infrastructure;

import com.fabien.restaurant_booking_api.customer.application.CustomerMapper;
import com.fabien.restaurant_booking_api.customer.application.CustomerRequest;
import com.fabien.restaurant_booking_api.customer.application.CustomerResponse;
import com.fabien.restaurant_booking_api.customer.application.CustomerService;
import com.fabien.restaurant_booking_api.customer.domain.Customer;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin("http://localhost:4200")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @GetMapping
  public ResponseEntity<List<CustomerResponse>> findAll() {
    List<CustomerResponse> customerResponses = customerService.findAll()
        .stream()
        .map(CustomerMapper::toResponse)
        .toList();
    return ResponseEntity.ok(customerResponses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
    Customer customer = customerService.findById(id);

    return ResponseEntity.ok(CustomerMapper.toResponse(customer));
  }

  @PostMapping
  public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
    Customer customer = CustomerMapper.toEntity(request);

    CustomerResponse response = CustomerMapper.toResponse(customerService.create(customer));
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.id())
        .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
      @Valid @RequestBody CustomerRequest request) {
    Customer updatedCustomer = customerService.update(id, CustomerMapper.toEntity(request));

    return ResponseEntity.ok(CustomerMapper.toResponse(updatedCustomer));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    customerService.deleteById(id);

    return ResponseEntity.noContent().build();
  }
}
