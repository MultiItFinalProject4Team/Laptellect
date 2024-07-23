package com.multi.laptellect.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(){
        return "hello";
    }

    @GetMapping("/hello")
    public String hellopage(){
        return "hello";
    }


}