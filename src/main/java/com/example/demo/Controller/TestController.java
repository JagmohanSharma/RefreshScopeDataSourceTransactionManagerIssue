package com.example.demo.Controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest/v1")
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping(method = RequestMethod.GET, value = "/save")
    public Customer save() throws Exception {
        return testService.save(new Customer("Jack", "Smith"));
    }

}
