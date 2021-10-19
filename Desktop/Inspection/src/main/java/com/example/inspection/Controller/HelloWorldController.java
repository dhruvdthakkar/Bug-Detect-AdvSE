package com.example.inspection.Controller;

import com.example.inspection.DTO.Product;
import com.example.inspection.DTO.Snippet;
import com.example.inspection.fix.RootFix;
import com.example.inspection.util.GetResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class HelloWorldController
{
    @GetMapping("/upload")
    public String helloWorld() {
        return "upload.html";
    }

    @GetMapping("/result")
    public String result() {
        RootFix.helper();
        return "result";
    }

    @GetMapping("/snippet")
    public String newProduct() {
        return "snippet";
    }

    @GetMapping("/github")
    public String github(){
        return "github.html";
    }

    @GetMapping("/")
    public String index() {
        return "welcome.html";
    }
}
