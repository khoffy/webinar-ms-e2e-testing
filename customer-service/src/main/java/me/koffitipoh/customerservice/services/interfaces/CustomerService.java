package me.koffitipoh.customerservice.services.interfaces;

import me.koffitipoh.customerservice.dto.CustomerDTO;
import me.koffitipoh.customerservice.exceptions.CustomerNotFoundException;
import me.koffitipoh.customerservice.exceptions.EmailAlreadyExistsException;

import java.util.List;

public interface CustomerService {
    CustomerDTO saveNewCustomer(CustomerDTO customerDTO) throws EmailAlreadyExistsException;
    List<CustomerDTO> getAllCustomers();
    CustomerDTO findCustomerById(Long id) throws CustomerNotFoundException;
    List<CustomerDTO> searchCustomers(String keyword);
    CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO) throws CustomerNotFoundException;
    void deleteCustomer(Long id) throws CustomerNotFoundException;
}
