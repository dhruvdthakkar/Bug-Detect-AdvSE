package com.example.inspection.fix;

import com.example.inspection.DTO.Snippet;

public class ParserFotCode {
    public static void helper(Snippet snippet) {
        // Arithmetic.arithmetic();
        // NullPointer.nullCheck();
        String tempS = snippet.getContent();
        if (tempS.contains("/")) {
            String[] airmen = tempS.split("/");
            if (airmen[1].charAt(0) == '0') {
                System.out.println("----0 would cause an error----");
            }
        }

        String[] temp = null;

        if (tempS.contains("null")) {
            String[] inspect = tempS.split("null");
            String[] inspect2 = inspect[0].split(" ");
            temp = inspect2[inspect2.length - 1].split("=");
        } else if (temp != null) {
            if (tempS.contains(temp[0])) {
                System.out.println("----Used null reference which cause an error later!---");
            }
        }
    }
}
