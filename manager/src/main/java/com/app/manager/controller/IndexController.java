package com.app.manager.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam(value="name",defaultValue = "world") String name) {
        return String.format("hellow %s!",name);
    }




    public String ipaFileUpload(){
        return "game";
    }
}
