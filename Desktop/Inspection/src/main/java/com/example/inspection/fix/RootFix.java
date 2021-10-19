package com.example.inspection.fix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RootFix {
    public static void helper(){
        // Arithmetic.arithmetic();
        // NullPointer.nullCheck();
        try {
            File myObj1 = new File("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\src\\main\\java\\com\\example\\inspection\\fix\\Arithmetic.java");
            File myObj2 = new File("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\src\\main\\java\\com\\example\\inspection\\fix\\NullPointer.java");
            Scanner myReader1 = new Scanner(myObj1);
            Scanner myReader2 = new Scanner(myObj2);
            while (myReader1.hasNextLine()) {
                String data = myReader1.nextLine();
                if (data.contains("/")){
                    String airmen[] = data.split("/");
                    if (airmen[1].charAt(0) == '0'){
                        System.out.println("----0 would cause an error----");
                    }
                }
            }
            String[] temp = null;
            while (myReader2.hasNextLine()) {
                String data = myReader2.nextLine();
                if (data.contains("null")) {
                    String[] inspect = data.split("null");
                    String[] inspect2 = inspect[0].split(" ");
                    temp = inspect2[inspect2.length - 1].split("=");
                }
                else if(temp != null){
                    if (data.contains(temp[0])){
                        System.out.println("----Used null reference which cause an error later!---");
                    }
                }
            }
            myReader1.close();
            myReader2.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
