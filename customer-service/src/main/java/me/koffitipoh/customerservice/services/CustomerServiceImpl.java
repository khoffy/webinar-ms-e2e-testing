package me.koffitipoh.customerservice.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import me.koffitipoh.customerservice.dto.CustomerDTO;
import me.koffitipoh.customerservice.entities.Customer;
import me.koffitipoh.customerservice.exceptions.CustomerNotFoundException;
import me.koffitipoh.customerservice.exceptions.EmailAlreadyExistsException;
import me.koffitipoh.customerservice.mapper.CustomerMapper;
import me.koffitipoh.customerservice.repositories.CustomerRepository;
import me.koffitipoh.customerservice.services.interfaces.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author khoffy
 *
 * @version 1.0
 * @since 2/03/2024
 */
@Service
@Transactional
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;


    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) throws EmailAlreadyExistsException {
        log.info(String.format("Saving new customer => %s", customerDTO.toString()));
        if(customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            log.info(String.format("This email %s already exists", customerDTO.getEmail()));
            throw new EmailAlreadyExistsException();
        }
        Customer customer = customerMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.fromCustomer(savedCustomer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.fromListCustomers(customers);
    }

    @Override
    public CustomerDTO findCustomerById(Long id) throws CustomerNotFoundException {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isEmpty()) throw new CustomerNotFoundException();
        return customerMapper.fromCustomer(customer.get());
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> byFirstNameContainsIgnoreCase = customerRepository.findByFirstNameContainsIgnoreCase(keyword);
        return customerMapper.fromListCustomers(byFirstNameContainsIgnoreCase);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) throws CustomerNotFoundException {
        Optional<Customer> optionalOfCustomer = customerRepository.findById(customerDTO.getId());
        if (optionalOfCustomer.isEmpty()) throw new CustomerNotFoundException();
        Customer customer = optionalOfCustomer.get();
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        return customerMapper.fromCustomer(customerRepository.saveAndFlush(customer));
    }

    @Override
    public void deleteCustomer(Long id) throws CustomerNotFoundException {
        Optional<Customer> byId = customerRepository.findById(id);
        if(byId.isEmpty()) throw new CustomerNotFoundException();
        customerRepository.delete(byId.get());
    }
    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setCustomerMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }
}
