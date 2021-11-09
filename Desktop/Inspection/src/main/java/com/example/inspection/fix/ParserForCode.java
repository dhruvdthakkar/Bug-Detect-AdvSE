package com.example.inspection.fix;

import com.example.inspection.DTO.Snippet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ParserForCode {
    public static void helper(Snippet snippet) {
        try {
            File myObj = new File("C://Users//Dhruv Thakkar//Desktop//Inspection//src//main//resources//static//filename4.java");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter writeIn = new FileWriter("C://Users//Dhruv Thakkar//Desktop//Inspection//src//main//resources//static//filename4.java");
            writeIn.write(snippet.getContent());
            writeIn.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
