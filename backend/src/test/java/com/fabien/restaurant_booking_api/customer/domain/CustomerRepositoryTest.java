package com.fabien.restaurant_booking_api.customer.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CustomerRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private CustomerRepository customerRepository;

  @Test
  void save_should_generate_id_and_persist_customer() {
    //Given
    Customer customer = createCustomer("Testeur", "test@test.com", "99-99-99-99-99");

    //When
    Customer saved = customerRepository.save(customer);

    //Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Testeur");
    assertThat(saved.getEmail()).isEqualTo("test@test.com");
    assertThat(saved.getPhoneNumber()).isEqualTo("99-99-99-99-99");
  }

  @Test
  void findById_should_return_customer_when_exists() {
    // Given
    Customer customer = createCustomer("Testeur", "test@test.com", "99-99-99-99-99");
    Customer saved = entityManager.persistAndFlush(customer);

    // When
    Optional<Customer> found = customerRepository.findById(saved.getId());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Testeur");
  }

  @Test
  void findById_should_return_empty_when_customer_not_exists() {
    // Given
    Long nonExistentId = 999L;

    // When
    Optional<Customer> found = customerRepository.findById(nonExistentId);

    // Then
    assertThat(found).isEmpty();
  }

  private Customer createCustomer(String name, String email, String phoneNumber) {
    Customer customer = new Customer();
    customer.setName(name);
    customer.setEmail(email);
    customer.setPhoneNumber(phoneNumber);

    return customer;
  }

}