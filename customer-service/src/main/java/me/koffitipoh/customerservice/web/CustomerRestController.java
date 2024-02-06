package me.koffitipoh.customerservice.web;

import me.koffitipoh.customerservice.dto.CustomerDTO;
import me.koffitipoh.customerservice.services.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author khoffy
 *
 * @version 1.0
 * @since 2/3/2024
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {
    private CustomerServiceImpl customerService;
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.OK);
    }
    @GetMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> getOneById(@PathVariable Long id) {
        return new ResponseEntity<>(customerService.findCustomerById(id), HttpStatus.OK);
    }
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchByKeyword(@RequestParam String keyword) {
        return new ResponseEntity<>(customerService.searchCustomers(keyword), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<CustomerDTO> createNewCustomer(@RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(customerService.saveNewCustomer(customerDTO), HttpStatus.CREATED);
    }
    @PutMapping("/customer/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long customerId,
                                                      @RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(customerService.updateCustomer(customerId, customerDTO), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Resource deleted successfully!");
    }
    @Autowired
    public void setCustomerService(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }
}
