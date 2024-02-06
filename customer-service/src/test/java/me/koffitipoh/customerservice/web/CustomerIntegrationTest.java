package me.koffitipoh.customerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.koffitipoh.customerservice.dto.CustomerDTO;
import me.koffitipoh.customerservice.entities.Customer;
import me.koffitipoh.customerservice.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Profile;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
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
            .withDatabaseName("customers-db")
            .withUsername("khoffy")
            .withPassword("1234");
    List<CustomerDTO> customerDTOs;
    @BeforeEach
    void setUp() {
        this.customerDTOs = new ArrayList<>();
        this.customerDTOs.add(CustomerDTO.builder()
                .id(1L).firstName("Nimon").lastName("Maaleki").email("nimon.maaleki@gmail.com").build());
        this.customerDTOs.add(CustomerDTO.builder()
                .id(2L).firstName("mensanh").lastName("Namessi").email("mensanh.namessi@gmail.com").build());
        this.customerDTOs.add(CustomerDTO.builder()
                .id(3L).firstName("Marchal").lastName("Teach D.").email("marchal.teachD@gmail.com").build());
    }

    @Test
    @Rollback
    void shouldSaveValidNewCustomer() {
        CustomerDTO customerDTO =
                CustomerDTO.builder().firstName("Jean").lastName("Dupont").email("jean.dupont@gmail.com").build();
        ResponseEntity<CustomerDTO> response =
                testRestTemplate.exchange("/api/customers", HttpMethod.POST,
                        new HttpEntity<>(customerDTO), CustomerDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(customerDTO);
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
}