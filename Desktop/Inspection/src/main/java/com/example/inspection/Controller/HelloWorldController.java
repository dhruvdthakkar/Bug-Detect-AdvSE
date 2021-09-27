package com.example.inspection.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HelloWorldController
{
    @GetMapping("/upload")
    public String helloWorld() {
        return "upload";
    }

    @GetMapping("/result")
    public String result() {
        return "result";
    }

    @GetMapping("/snippet")
    public String snippet(){
        return "snippet";
    }

    @GetMapping("/github")
    public String github(){
        return "github";
    }

    @GetMapping("/")
    public String index() {
        return "welcome";
    }
}