package com.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratios")
public class RatiosController {



    @GetMapping
    @RequestMapping("/get")
    public void get() {
//        return new Test();
//        System.out.println(ndAll());
//        return List.of(usersRepository.findAll());
    }
}
