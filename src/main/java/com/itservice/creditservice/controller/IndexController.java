package com.itservice.creditservice.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/credits")
public class IndexController {
	
	
    
    @GetMapping("/index")
    public String index() {
    	return "uploader";
    }
    
    
    @GetMapping("/index-kfk")
    public String index_Kafka() {
    	return "uploader-kfk";
    }
    
    

    

}
