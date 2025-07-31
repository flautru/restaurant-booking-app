package com.fabien.restaurant_booking_api.customer.application;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomer;
import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomerWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.customer.domain.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  private CustomerService customerService;

  @BeforeEach
  void setUp() {
    customerService = new CustomerService(customerRepository);
  }

  @Test
  void findAll_should_return_all_customer() {
    Customer c1 = createTestCustomerWithId(1L, "JeanTest", "jean@test.com", "00-02-03-04-05");
    Customer c2 = createTestCustomerWithId(2L, "MarieTest", "marie@test.com", "01-02-03-04-05");

    when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

    List<Customer> results = customerService.findAll();
    assertThat(results).hasSize(2)
        .extracting(Customer::getId).containsExactly(1L, 2L);

    assertThat(results).extracting(Customer::getName)
        .containsExactly("JeanTest", "MarieTest");

    assertThat(results).extracting(Customer::getEmail)
        .containsExactly("jean@test.com", "marie@test.com");

    assertThat(results).extracting(Customer::getPhoneNumber)
        .containsExactly("00-02-03-04-05", "01-02-03-04-05");

    verify(customerRepository).findAll();
  }

  @Test
  void findAll_should_empty_list_when_no_customer() {

    when(customerRepository.findAll()).thenReturn(List.of());

    List<Customer> results = customerService.findAll();

    assertThat(results).isEmpty();
    verify(customerRepository).findAll();
  }

  @Test
  void findById_should_return_customer_when_exist() {
    Customer c1 = createTestCustomerWithId(1L, "JeanTest", "jean@test.com", "00-02-03-04-05");

    when(customerRepository.findById(1L)).thenReturn(Optional.of(c1));

    Customer result = customerService.findById(1L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("JeanTest");
    assertThat(result.getEmail()).isEqualTo("jean@test.com");
    assertThat(result.getPhoneNumber()).isEqualTo("00-02-03-04-05");

    verify(customerRepository).findById(1L);
  }

  @Test
  void findById_should_throw_entityNotFound_when_not_exists() {
    Long nonExistentId = 999L;
    when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> customerService.findById(nonExistentId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("Customer not found with id : " + nonExistentId);

    verify(customerRepository).findById(nonExistentId);
  }

  @Test
  void create_should_save_and_return_customer() {
    Customer c1 = createTestCustomer("JeanTest", "jean@test.com", "00-02-03-04-05");

    when(customerRepository.save(any(Customer.class))).thenReturn(c1);

    Customer saved = customerService.create(c1);

    assertThat(saved).isNotNull();
    assertThat(saved.getId()).isEqualTo(c1.getId());
    assertThat(saved.getName()).isEqualTo(c1.getName());
    assertThat(saved.getEmail()).isEqualTo(c1.getEmail());
    assertThat(saved.getPhoneNumber()).isEqualTo(c1.getPhoneNumber());

    verify(customerRepository).save(any(Customer.class));
  }

  @Test
  void create_should_throw_when_phone_number_already_exists() {
    Customer customer = createTestCustomer();

    when(customerRepository.save(any(Customer.class)))
        .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

    assertThatThrownBy(() -> customerService.create(customer))
        .isInstanceOf(DataIntegrityViolationException.class);

    verify(customerRepository).save(any(Customer.class));
  }

  @Test
  void update_should_verify_existence_and_save_customer() {
    Customer c1 = createTestCustomerWithId(1L);
    Customer expected = createTestCustomerWithId(1L, "MarieTest", "marie@test.com",
        "00-02-03-04-05");

    when(customerRepository.findById(1L)).thenReturn(Optional.of(c1));
    when(customerRepository.save(any(Customer.class))).thenReturn(expected);

    Customer saved = customerService.update(1L, c1);

    assertThat(saved).isNotNull();
    assertThat(saved.getId()).isEqualTo(expected.getId());
    assertThat(saved.getName()).isEqualTo(expected.getName());
    assertThat(saved.getEmail()).isEqualTo(expected.getEmail());
    assertThat(saved.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());

    verify(customerRepository).findById(1L);
    verify(customerRepository).save(any(Customer.class));
  }

  @Test
  void deleteById_should_verify_existence_and_delete_customer() {
    Customer c1 = createTestCustomerWithId(1L);

    when(customerRepository.findById(1L)).thenReturn(Optional.of(c1));

    customerService.deleteById(1L);

    verify(customerRepository).findById(1L);
    verify(customerRepository).deleteById(1L);

  }
}