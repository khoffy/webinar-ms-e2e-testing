package me.koffitipoh.customerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.koffitipoh.customerservice.dto.CustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CustomerIntegrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("customers_db")
            .withUsername("koffi")
            .withPassword("1234");
    List<CustomerDTO> customerDTOs;
    @BeforeEach
    void setUp() {
        this.customerDTOs = new ArrayList<>();
        this.customerDTOs.add(CustomerDTO.builder()
                .id(1L).firstName("Koffiah").lastName("Tipoh").email("koffiah.tipoh@gmail.com").build());
        this.customerDTOs.add(CustomerDTO.builder()
                .id(2L).firstName("Luffy").lastName("Monkey D.").email("luffy.mokeyD@gmail.com").build());
        this.customerDTOs.add(CustomerDTO.builder()
                .id(3L).firstName("Naruto").lastName("Uzumaki").email("naruto.uzumaki@gmail.com").build());

    }
    @Test
    @Rollback
    void shouldSaveValidNewCustomer() {
        CustomerDTO customerDTO =
                CustomerDTO.builder().firstName("John").lastName("Dupont").email("jean.dupont@gmail.com").build();
        ResponseEntity<CustomerDTO> response =
                testRestTemplate.exchange("/api/customers", HttpMethod.POST,
                        new HttpEntity<>(customerDTO), CustomerDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(customerDTO);
    }

    @Test
    @Rollback
    void shouldNotSaveInvalidCustomer() throws com.fasterxml.jackson.core.JsonProcessingException {
        CustomerDTO customerDTO = CustomerDTO.builder().firstName("").lastName("").email("").build();
        ResponseEntity<String> response = testRestTemplate.exchange("/api/customers", HttpMethod.POST,
                new HttpEntity<>(customerDTO), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, ArrayList<String>> errors = objectMapper.readValue(response.getBody(), HashMap.class);
        assertThat(errors.keySet().size()).isEqualTo(3);
        assertThat(errors.get("firstName").size()).isEqualTo(2);
        assertThat(errors.get("lastName").size()).isEqualTo(2);
        assertThat(errors.get("email").size()).isEqualTo(1);
    }
    @Test
    void shouldGetAllCustomers() {
        ResponseEntity<CustomerDTO[]> response = testRestTemplate.exchange("/api/customers", HttpMethod.GET,
                null, CustomerDTO[].class);
        List<CustomerDTO> content = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(content.size()).isEqualTo(4);
        //assertThat(content).usingRecursiveComparison().isEqualTo(customerDTOs);
    }
    @Test
    void shouldSearchCustomersByFirstName() {
        String keyword = "a";
        ResponseEntity<CustomerDTO[]> response =
                testRestTemplate.exchange("/api/customers/search?keyword=" + keyword, HttpMethod.GET,
                        null, CustomerDTO[].class);
        List<CustomerDTO> content = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(content.size()).isEqualTo(2);
        List<CustomerDTO> expected = customerDTOs.stream().filter(c ->
                c.getFirstName().toLowerCase().contains(keyword.toLowerCase())).toList();
        assertThat(content).usingRecursiveComparison().isEqualTo(expected);
    }
    @Test
    void shouldGetCustomerById() {
        long customerId = 1L;
        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange("/api/customers/customer/" +customerId,
                HttpMethod.GET, null, CustomerDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(customerDTOs.getFirst());
    }

    @Test
    void shouldNotFindCustomerById() {
        long customerId = 9L;
        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange("/api/customers/customer/" + customerId,
                HttpMethod.GET, null, CustomerDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    @Rollback
    void shouldUpdateValidCustomer() {
        long customerId = 2L;
        CustomerDTO customerDTO =
                CustomerDTO.builder().id(2L).firstName("Hanane").lastName("Yamal").email("han@gmail.com").build();
        ResponseEntity<CustomerDTO> response =
                testRestTemplate.exchange("/api/customers/customer/" + customerId,
                        HttpMethod.PUT, new HttpEntity<>(customerDTO), CustomerDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(customerDTO);
    }

    @Test
    @Rollback
    void shouldDeleteCustomer() {
        long customerId = 1L;
        ResponseEntity<String> response = testRestTemplate.exchange("/api/customers/" + customerId,
                HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Resource deleted successfully!");
    }
}

