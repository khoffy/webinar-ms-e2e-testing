package me.koffitipoh.customerservice.services;

import me.koffitipoh.customerservice.dto.CustomerDTO;
import me.koffitipoh.customerservice.entities.Customer;
import me.koffitipoh.customerservice.exceptions.CustomerNotFoundException;
import me.koffitipoh.customerservice.exceptions.EmailAlreadyExistsException;
import me.koffitipoh.customerservice.mapper.CustomerMapper;
import me.koffitipoh.customerservice.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplITest {

    @Mock
    private CustomerRepository mockCustomerRepository;

    @Mock
    private CustomerMapper mockCustomerMapper;

    @InjectMocks
    private CustomerServiceImpl customerServiceUnderTest;

    @Test
    void shouldSaveNewCustomer() {
        // Given
        CustomerDTO customerDTO =
                CustomerDTO.builder().firstName("Luffy").lastName("Monkey D.").email("luffymonkeyD@gmail.com")
                        .build();
        Customer customer =
                Customer.builder().firstName("Luffy").lastName("Monkey D.").email("luffy.monkeyD@gmail.com")
                        .build();
        Customer customerWithId =
                Customer.builder().id(1L).firstName("Luffy").lastName("Monkey D.").email("luffy.monkeyD@gmail.com")
                        .build();
        CustomerDTO expected =
                CustomerDTO.builder().id(1L).firstName("Luffy").lastName("Monkey D.").email("luffy.monkeyD@gmail.com")
                        .build();
        when(mockCustomerRepository.findByEmail(customerDTO.getEmail())).thenReturn(Optional.empty());
        when(mockCustomerMapper.fromCustomerDTO(customerDTO)).thenReturn(customer);
        when(mockCustomerRepository.save(customer)).thenReturn(customerWithId);
        when(mockCustomerMapper.fromCustomer(customerWithId)).thenReturn(expected);

        // When
        CustomerDTO savedCustomer = customerServiceUnderTest.saveNewCustomer(customerDTO);

        // Then
        assertThat(savedCustomer).usingRecursiveComparison().isEqualTo(expected);

    }

    @Test
    void shouldNotSaveNewCustomerWhenEmailExist() {
        // Given
        CustomerDTO customerDTO =
                CustomerDTO.builder().firstName("Adjo").lastName("Agbale").email("adjo.agbale@gmail.com").build();
        Customer customer =
                Customer.builder().id(1L).firstName("Adjo").lastName("Agbale").email("adjo.agbale@gmail.com").build();

        when(mockCustomerRepository.findByEmail(customerDTO.getEmail())).thenReturn(Optional.of(customer));
        // Then
        assertThatThrownBy(() -> customerServiceUnderTest.saveNewCustomer(customerDTO))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void shouldGetAllCustomers() {
        // Given
        List<Customer> customers = List.of(
                Customer.builder().id(1L).firstName("Jack").lastName("Dupont").email("jackdupont@gmail.com").build(),
                Customer.builder().id(2L).firstName("Jacky").lastName("Dupont").email("jackydupont@gmail.com").build(),
                Customer.builder().id(3L).firstName("Jackeline").lastName("Dupont").email("jackelinedupont@gmail.com").build()
        );
        List<CustomerDTO> customerDTOs = List.of(
                CustomerDTO.builder().id(1L).firstName("Jack").lastName("Dupont").email("jackdupont@gmail.com").build(),
                CustomerDTO.builder().id(2L).firstName("Jacky").lastName("Dupont").email("jackydupont@gmail.com").build(),
                CustomerDTO.builder().id(3L).firstName("Jackeline").lastName("Dupont").email("jackelinedupont@gmail.com").build()
        );

        when(mockCustomerRepository.findAll()).thenReturn(customers);
        when(mockCustomerMapper.fromListCustomers(customers)).thenReturn(customerDTOs);

        // When
        List<CustomerDTO> result = customerServiceUnderTest.getAllCustomers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(customerDTOs);
    }

    @Test
    void shouldFindCustomerById() {
        // Given
        Long customerId = 1L;
        Customer customer =
                Customer.builder().id(1L).firstName("Remy").lastName("Gorges").email("remy@gmail.com")
                    .build();
        CustomerDTO expected =
                CustomerDTO.builder().id(1L).firstName("Remy").lastName("Gorges").email("remy@gmail.com")
                        .build();
        when(mockCustomerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(mockCustomerMapper.fromCustomer(customer)).thenReturn(expected);

        // When
        CustomerDTO customerById = customerServiceUnderTest.findCustomerById(1L);
        // Then
        assertThat(customerById).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldNotFindCustomerById() {
        // Given
        Long customerId = 8L;
        when(mockCustomerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> customerServiceUnderTest.findCustomerById(customerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}