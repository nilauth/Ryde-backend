package com.cosmos.usersmanagementsystem;

import com.cosmos.usersmanagementsystem.entity.OurUsers;
import com.cosmos.usersmanagementsystem.repository.UsersRepo;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class UsersmanagementsystemApplication {

	@Bean
	@Primary
	public BCryptPasswordEncoder mainPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//@Bean
	CommandLineRunner commandLineRunner(UsersRepo usersRepo, BCryptPasswordEncoder mainPasswordEncoder) {
		return args -> {
			usersRepo.save(OurUsers.builder()
					.id(null)
					.cin("12345678")
					.email("admin@example.com")
					.name("admin")
					.password(mainPasswordEncoder.encode("12345678"))
					.city("fes")
					.role("ADMIN")
					.solde(0.0)
					.build());
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(UsersmanagementsystemApplication.class, args);
	}
}