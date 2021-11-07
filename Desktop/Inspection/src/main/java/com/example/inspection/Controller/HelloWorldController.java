package com.example.inspection.Controller;

import com.example.inspection.DTO.Snippet;
import com.example.inspection.fix.ParserFotCode;
import com.example.inspection.fix.RootFix;
import org.springframework.stereotype.Controller;
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
//        RootFix.helper(snippet);
        return "result";
    }

    @GetMapping("/snippet")
    public String newProduct(Snippet snippet) {
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

    @GetMapping("/test")
    public String test(Snippet snippet){
        return "FormTest";
    }

    @PostMapping("/resultSnippet")
    public String testResultSnippet (Snippet snippet){
        ParserFotCode.helper(snippet);
        return "testt";
    }

}
