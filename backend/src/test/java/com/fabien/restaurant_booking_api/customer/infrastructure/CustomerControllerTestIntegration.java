package com.fabien.restaurant_booking_api.customer.infrastructure;

import static com.fabien.restaurant_booking_api.shared.utils.TestDataBuilder.createTestCustomer;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fabien.restaurant_booking_api.customer.domain.Customer;
import com.fabien.restaurant_booking_api.customer.domain.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTestIntegration {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CustomerRepository customerRepository;

  @AfterEach
  void tearDown() {
    customerRepository.deleteAll();
  }

  @Test
  void findAll_should_return_all_customers_with_200() throws Exception {
    // Given
    Customer c1 = createTestCustomer("JeanTest", "jean@test.com", "00-02-03-04-05");
    Customer c2 = createTestCustomer("MarieTest", "marie@test.com", "01-02-03-04-05");

    customerRepository.save(c1);
    customerRepository.save(c2);

    // When & Then
    mockMvc.perform(get("/api/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name", is("JeanTest")))
        .andExpect(jsonPath("$[0].email", is("jean@test.com")))
        .andExpect(jsonPath("$[0].phoneNumber", is("00-02-03-04-05")))
        .andExpect(jsonPath("$[1].name", is("MarieTest")))
        .andExpect(jsonPath("$[1].email", is("marie@test.com")))
        .andExpect(jsonPath("$[1].phoneNumber", is("01-02-03-04-05")));
  }

  @Test
  void findAll_should_return_empty_list_when_no_customers() throws Exception {
    // Given

    // When & Then
    mockMvc.perform(get("/api/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void findById_should_return_customer_with_200_when_exists() throws Exception {
    // Given
    Customer customer = createTestCustomer("JeanTest", "jean@test.com", "00-02-03-04-05");
    Customer saved = customerRepository.save(customer);

    // When & Then
    mockMvc.perform(get("/api/customers/{id}", saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
        .andExpect(jsonPath("$.name", is("JeanTest")))
        .andExpect(jsonPath("$.email", is("jean@test.com")))
        .andExpect(jsonPath("$.phoneNumber", is("00-02-03-04-05")));
  }

  @Test
  void findById_should_return_404_when_customer_not_exists() throws Exception {
    // Given
    Long nonExistentId = 999L;

    // When & Then
    mockMvc.perform(get("/api/customers/{id}", nonExistentId))
        .andExpect(status().isNotFound());
  }

  @Test
  void create_should_create_customer_and_return_201_with_location() throws Exception {
    // Given
    String requestJson = """
        {
          "name": "JeanTest",
          "email": "jean@test.com",
          "phoneNumber": "00-02-03-04-05"
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", matchesPattern(".*/api/customers/\\d+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name", is("JeanTest")))
        .andExpect(jsonPath("$.email", is("jean@test.com")))
        .andExpect(jsonPath("$.phoneNumber", is("00-02-03-04-05")));
  }

  @Test
  void create_should_create_customer_and_return_201_with_location_when_email_is_blank()
      throws Exception {
    // Given
    String requestJson = """
        {
          "name": "JeanTest",
          "email": "",
          "phoneNumber": "00-02-03-04-05"
        }
        """;

    // When & Then
// When & Then
    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", matchesPattern(".*/api/customers/\\d+")))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name", is("JeanTest")))
        .andExpect(jsonPath("$.email", is("")))
        .andExpect(jsonPath("$.phoneNumber", is("00-02-03-04-05")));
  }

  @Test
  void create_should_return_400_when_phone_number_is_blank() throws Exception {
    // Given
    String requestJson = """
        {
          "name": "JeanTest",
          "email": "jean@test.com",
          "phoneNumber": ""
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_should_return_409_when_phone_number_already_exists() throws Exception {

    Customer customer = createTestCustomer("MarieTest", "", "00-02-03-04-05");
    customerRepository.save(customer);

    String requestJson = """
        {
          "name": "JeanTest",
          "email": "",
          "phoneNumber": "00-02-03-04-05"
        }
        """;

    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error", is("Une ressource avec ces données existe déjà")));
  }

  @Test
  void update_should_return_409_when_phone_number_conflicts_with_other_customer() throws Exception {
    // Given
    Customer c1 = createTestCustomer("Jean", "jean@test.com", "00-02-03-04-05");
    Customer c2 = createTestCustomer("Marie", "marie@test.com", "01-02-03-04-05");

    Customer savedC1 = customerRepository.saveAndFlush(c1);
    customerRepository.saveAndFlush(c2);

    String requestJson = """
        {
          "name": "Jean Updated",
          "email": "jean@test.com",
          "phoneNumber": "01-02-03-04-05"
        }
        """;

    // When
    mockMvc.perform(put("/api/customers/" + savedC1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isConflict());
  }

  @Test
  void update_should_return_400_when_request_is_invalid() throws Exception {

    Customer customer = createTestCustomer("MarieTest", "", "00-02-03-04-05");
    Customer saved = customerRepository.save(customer);

    String invalidJson = """
        {
          "name": "",
          "email": "",
          "phoneNumber": "00-02-03-04-05"
        }
        """;

    // When & Then
    mockMvc.perform(put("/api/customers/{id}", saved.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void delete_should_delete_customer_and_return_204() throws Exception {
    // Given
    Customer customer = createTestCustomer("À Supprimer", "supprime@mail.com", "01-11-11-11-11");
    Customer saved = customerRepository.save(customer);

    // When & Then
    mockMvc.perform(delete("/api/customers/{id}", saved.getId()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/customers/{id}", saved.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_should_return_404_when_customer_not_exists() throws Exception {
    // Given
    Long nonExistentId = 999L;

    // When & Then
    mockMvc.perform(delete("/api/customers/{id}", nonExistentId))
        .andExpect(status().isNotFound());
  }
}