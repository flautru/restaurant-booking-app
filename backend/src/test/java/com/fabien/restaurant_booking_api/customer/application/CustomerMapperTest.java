package com.fabien.restaurant_booking_api.customer.application;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomerWithId;
import static org.assertj.core.api.Assertions.assertThat;

import com.fabien.restaurant_booking_api.customer.domain.Customer;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

  @Test
  void toResponse_should_return_customerResponse_when_valid_customer() {
    Customer c1 = createTestCustomerWithId(1L, "JeanTest", "jean@test.com", "00-02-03-04-05");

    CustomerResponse response = CustomerMapper.toResponse(c1);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(c1.getId());
    assertThat(response.name()).isEqualTo(c1.getName());
    assertThat(response.email()).isEqualTo(c1.getEmail());
    assertThat(response.phoneNumber()).isEqualTo(c1.getPhoneNumber());

  }

  @Test
  void toResponse_should_return_null_when_null_customer() {

    CustomerResponse response = CustomerMapper.toResponse(null);

    assertThat(response).isNull();
  }

  @Test
  void toEntity_should_return_customer_when_valid_customerRequest() {
    CustomerRequest c1 = createTestCustomerRequest("JeanTest", "jean@test.com", "00-02-03-04-05");

    Customer customer = CustomerMapper.toEntity(c1);

    assertThat(customer).isNotNull();
    assertThat(customer.getId()).isNull();
    assertThat(customer.getName()).isEqualTo(c1.getName());
    assertThat(customer.getEmail()).isEqualTo(c1.getEmail());
    assertThat(customer.getPhoneNumber()).isEqualTo(c1.getPhoneNumber());

  }

  @Test
  void toEntity_should_return_null_when_null_customerRequest() {

    Customer customer = CustomerMapper.toEntity(null);

    assertThat(customer).isNull();
  }

  private CustomerRequest createTestCustomerRequest(String name, String email, String phoneNumber) {
    CustomerRequest request = new CustomerRequest();
    request.setPhoneNumber(phoneNumber);
    request.setEmail(email);
    request.setName(name);

    return request;
  }
}