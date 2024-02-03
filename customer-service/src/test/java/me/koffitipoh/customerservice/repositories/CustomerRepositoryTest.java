package me.koffitipoh.customerservice.repositories;

import me.koffitipoh.customerservice.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        customerRepository.save(
                Customer.builder().firstName("Koffi").lastName("Tipoh").email("koffiah.tipoh@gmail.com")
                        .build());
        customerRepository.save(
                Customer.builder().firstName("Luffy").lastName("Monkey D.").email("luffy.monkeyD@gmail.com")
                        .build());
        customerRepository.save(
                Customer.builder().firstName("Naruto").lastName("Uzumaki").email("naruto.uzumaki@gmail.com")
                        .build());
    }

    @Test
    void shouldFindCustomerByEmail() {
        String givenEmail = "koffiah.tipoh@gmail.com";
        Optional<Customer> retrieveCustomer = customerRepository.findByEmail(givenEmail);

        assertThat(retrieveCustomer).isPresent();
    }
    @Test
    void shouldNotFindCustomerByEmail() {
        String givenEmail = "koffia.tipoh@gmail.com";
        Optional<Customer> retrieveCustomer = customerRepository.findByEmail(givenEmail);

        assertThat(retrieveCustomer).isEmpty();
    }


    @Test
    void shouldFindCustomerByFirstName() {
        String keyword = "o";
        List<Customer> expected = List.of(
                Customer.builder().firstName("Koffi").lastName("Tipoh").email("koffiah.tipoh@gmail.com")
                                .build(),
                Customer.builder().firstName("Naruto").lastName("Uzumaki").email("naruto.uzumaki@gmail.com")
                        .build()
        );

        List<Customer> result = customerRepository.findByFirstNameContainsIgnoreCase(keyword);
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }
}