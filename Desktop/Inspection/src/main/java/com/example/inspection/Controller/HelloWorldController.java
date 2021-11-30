package com.example.inspection.Controller;

import com.example.inspection.DTO.Result;
import com.example.inspection.DTO.Snippet;
import com.example.inspection.bug_detect.Main;
import com.example.inspection.util.ParserForCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
public class HelloWorldController
{
    private static String UPLOADED_FOLDER = "C://Users//Dhruv Thakkar//Desktop//Inspection//src//main//resources//static//";

    @GetMapping("/upload")
    public String helloWorld() {
        return "upload";
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

    @PostMapping("/resultSnippet")
    public String testResultSnippet (Snippet snippet){
        ParserForCode.helper(snippet);
        return "redirect:/uploadStatus";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus(Result result) {
        Main.initSetup();
        return "C:/Users/Dhruv Thakkar/Desktop/Inspection/results/report.html";
    }
}
