package com.example.demo.repositories;

import com.example.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Customer, Long> {
}
