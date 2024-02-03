package me.koffitipoh.customerservice.repositories;

import me.koffitipoh.customerservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author khoffy
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    List<Customer> findByFirstNameContainsIgnoreCase(String keyword);
}
