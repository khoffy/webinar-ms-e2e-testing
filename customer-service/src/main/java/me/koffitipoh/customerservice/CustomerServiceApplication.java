package me.koffitipoh.customerservice;

import me.koffitipoh.customerservice.entities.Customer;
import me.koffitipoh.customerservice.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class CustomerServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Profile("!test")
	@Bean
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
		return args -> {
			customerRepository.save(
					Customer.builder().firstName("Koffiah").lastName("Tipoh").email("koffiah.tipoh@gmail.com")
							.build());
			customerRepository.save(
					Customer.builder().firstName("Luffy").lastName("Monkey D.").email("luffy.monkeyD@gmail.com")
							.build());
			customerRepository.save(
					Customer.builder().firstName("Naruto").lastName("Uzumaki").email("naruto.uzumaki@gmail.com")
							.build());
		};
	}
}
