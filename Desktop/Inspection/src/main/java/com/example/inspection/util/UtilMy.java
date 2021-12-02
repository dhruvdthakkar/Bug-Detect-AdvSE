package com.example.inspection.util;

import java.io.*;
import java.util.Scanner;

public class UtilMy {
    public void fileWrite(){
        //File file = new File("../../../results/report.html");
        System.out.println("Hit in write");
        StringBuilder sb = new StringBuilder();
        try {
            File myObj = new File("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\results\\report.html");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String dataTemp = myReader.nextLine();
                sb.append(dataTemp);
            }
            myReader.close();
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            File myObj = new File("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\src\\main\\resources\\templates\\result.html");
            System.out.println(myObj.delete());
            System.out.println(myObj.createNewFile());
        }
        catch (Exception e){

        }

        try (FileWriter fw = new FileWriter("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\src\\main\\resources\\templates\\result.html");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
