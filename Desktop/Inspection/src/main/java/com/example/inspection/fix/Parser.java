package com.example.inspection.fix;

import com.example.inspection.DTO.Result;
import com.example.inspection.DTO.Snippet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Parser {
    public static List<String> helper(Result result){
        List<String> errors = new ArrayList<>();
        try {
            File folder = new File("C://Users//Dhruv Thakkar//Desktop//Inspection//src//main//resources//static");
            File[] listOfFiles = folder.listFiles();
            for (File f: listOfFiles){
                System.out.println(f.getName());
                Scanner myReader = new Scanner(f);
                List<String> customReader = new ArrayList<>();
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    customReader.add(data);
                }

                // Arithmetic warning.
                for (String s : customReader) {
                    if (s.contains("/")){
                        String[] airmen = s.split("/");
                        if (airmen.length > 1){
                            if (airmen[1].length() > 0 && airmen[1].charAt(0) == '0'){
                                errors.add("0 would cause an error.");
                            }
                        }
                    }
                }

                // Used null pointer reference.
                String[] temp = null;
                for (String s : customReader) {
                    if (s.contains("null")) {
                        String[] inspect = s.split("null");
                        String[] inspect2 = inspect[0].split(" ");
                        temp = inspect2[inspect2.length - 1].split("=");
                    }
                    else if(temp != null){
                        if (s.contains(temp[0])){
                            errors.add("Used null reference which cause an error later!");
                        }
                    }
                }

                // File not closed.
                boolean flagForClosingFile = false;
                boolean flagForClosingFile2 = false;
                for (String s : customReader) {
                    if (s.contains("File")){
                        flagForClosingFile2 = true;
                        if (s.contains("close")){
                            flagForClosingFile = true;
                        }
                    }
                }
                if (flagForClosingFile && flagForClosingFile2){
                    errors.add("File has not been closed!");
                }

                // Integer String check.
                String[] tempForParse = null;
                for (String s : customReader) {
                    if (s.contains("Integer.parseInt")){
                        tempForParse = s.split("\\(");
                        if (tempForParse.length > 1 && !Character.isDigit(tempForParse[1].charAt(0))){
                            errors.add("There will be Digit instead of String.");
                        }
                    }
                }

                myReader.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return errors;
    }
}
