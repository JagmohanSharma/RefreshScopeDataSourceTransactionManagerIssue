package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "testTransactionManager")
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

    @Override
    public Customer save(Customer customer) {
        return testRepository.save(customer);
    }
}
