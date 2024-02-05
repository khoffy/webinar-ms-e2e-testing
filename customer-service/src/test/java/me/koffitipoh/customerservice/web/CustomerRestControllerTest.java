package me.koffitipoh.customerservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.koffitipoh.customerservice.dto.CustomerDTO;
import me.koffitipoh.customerservice.exceptions.CustomerNotFoundException;
import me.koffitipoh.customerservice.services.CustomerServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebMvcTest(CustomerRestController.class)
class CustomerRestControllerTest {
    @MockBean
    private CustomerServiceImpl customerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    List<CustomerDTO> customers;

    @BeforeEach
    void setUp() {
        this.customers = List.of(
                CustomerDTO.builder().id(1L).firstName("Najib").lastName("Najib").email("najib@gmail.com").build(),
                CustomerDTO.builder().id(2L).firstName("Gerard").lastName("Gerard").email("gerard@gmail.com").build(),
                CustomerDTO.builder().id(3L).firstName("Hermann").lastName("Hermann").email("hermann@gmail.com").build()
        );
    }
    @Test
    void shouldGetAllCustomers() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(customers);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customers)));
    }
    @Test
    void shouldGetCustomerById() throws Exception {
        Long id = 1L;
        when(customerService.findCustomerById(id)).thenReturn(customers.getFirst());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/customer/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customers.getFirst())));
    }
    @Test
    void shouldNotGetCustomerByInvalidId() throws Exception {
        Long id = 8L;
        when(customerService.findCustomerById(id)).thenThrow(CustomerNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/customer/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));

    }
    @Test
    void shouldCreateNewCustomer() throws Exception{
        // Given
        CustomerDTO customerDTO =
                CustomerDTO.builder().firstName("Jacky").lastName("Chan").email("jacky.chan@gmail.com").build();
        CustomerDTO created =
                CustomerDTO.builder().id(4L).firstName("Jacky").lastName("Chan").email("jacky.chan@gmail.com").build();
        String expected = """
                {
                    "id":4, "firstName":"Jacky", "lastName":"Chan", "email":"jacky.chan@gmail.com"
                }
                """;
        when(customerService.saveNewCustomer(Mockito.any())).thenReturn(created);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expected));
    }
    @Test
    void shouldUpdateCustomer() throws Exception{
        Long customerId = 1L;
        CustomerDTO customerDTO = customers.getFirst();
        when(customerService.updateCustomer(Mockito.eq(customerId), Mockito.any())).thenReturn(customers.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/customers/customer/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTO)));
    }
    @Test
    void shouldDeleteCustomer() throws Exception{
        Long customerId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/customers/{customerId}", customerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Resource deleted successfully!"));
    }
}

