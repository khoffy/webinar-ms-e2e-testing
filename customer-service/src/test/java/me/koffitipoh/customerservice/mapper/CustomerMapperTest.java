package me.koffitipoh.customerservice.mapper;

import me.koffitipoh.customerservice.dto.CustomerDTO;
import me.koffitipoh.customerservice.entities.Customer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CustomerMapperTest {
    CustomerMapper customerMapperUnderTest = new CustomerMapper();

    @Test
    void shouldMapCustomerToCustomerDTO() {
        Customer givenCustomer = Customer.builder()
                .id(1L).firstName("Koffi").lastName("Tipoh").email("koffi.tipoh@gmail.com")
                .build();

        CustomerDTO expected = CustomerDTO.builder()
                .id(1L).firstName("Koffi").lastName("Tipoh").email("koffi.tipoh@gmail.com")
                .build();

        CustomerDTO result = customerMapperUnderTest.fromCustomer(givenCustomer);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
    @Test
    void shouldMapCustomerDTOToCustomer() {
        Customer expected = Customer.builder()
                .id(1L).firstName("Koffi").lastName("Tipoh").email("koffi.tipoh@gmail.com")
                .build();

        CustomerDTO givenCustomerDTO = CustomerDTO.builder()
                .id(1L).firstName("Koffi").lastName("Tipoh").email("koffi.tipoh@gmail.com")
                .build();

        Customer result = customerMapperUnderTest.fromCustomerDTO(givenCustomerDTO);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
    @Test
    void shouldMapListOfCustomersToListOfCustomerDTOs() {
        List<Customer> givenCustomers = List.of(
                Customer.builder().id(1L).firstName("Luffy").lastName("Monkey D.").email("luffy.monkeyD@gmail.com")
                        .build(),
                Customer.builder().id(2L).firstName("Naruto").lastName("Uzumaki").email("naruto.uzumaki@gmail.com")
                        .build(),
                Customer.builder().id(3L).firstName("Roronoa").lastName("Zorro").email("roronoa.zorro@gmail.com")
                        .build()
        );

        List<CustomerDTO> expected = List.of(
                CustomerDTO.builder().id(1L).firstName("Luffy").lastName("Monkey D.").email("luffy.monkeyD@gmail.com")
                        .build(),
                CustomerDTO.builder().id(2L).firstName("Naruto").lastName("Uzumaki").email("naruto.uzumaki@gmail.com")
                        .build(),
                CustomerDTO.builder().id(3L).firstName("Roronoa").lastName("Zorro").email("roronoa.zorro@gmail.com")
                        .build()
        );

        List<CustomerDTO> result = customerMapperUnderTest.fromListCustomers(givenCustomers);

        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
    @Test
    void shouldNotMapNullToCustomerDTO() {
        Customer givenCustomer = null;

        CustomerDTO expected = CustomerDTO.builder()
                .id(1L).firstName("Koffi").lastName("Tipoh").email("koffi.tipoh@gmail.com")
                .build();

        assertThatThrownBy(() -> customerMapperUnderTest.fromCustomer(givenCustomer))
                .isInstanceOf(IllegalArgumentException.class);
    }
}