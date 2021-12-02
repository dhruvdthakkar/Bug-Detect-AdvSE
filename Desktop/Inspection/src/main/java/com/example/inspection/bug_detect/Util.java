package com.example.inspection.bug_detect;


import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Util {


    private static void writeInFile(String content) throws IOException {
        try (FileWriter fw = new FileWriter("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\results\\report.html");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(content);
        }
    }


    static void deleteFiles() {
        File files = new File("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\results\\");
        for (File file: Objects.requireNonNull(files.listFiles())) {
            file.delete();
        }

    }


    static void generateReport(File path, List<BugPattern> bugPatterns) {
        try {
            String htmlString = new String(Files.readAllBytes(Paths.get("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\resources\\report-template.html")),
                    StandardCharsets.UTF_8);


            Map<String, BugPattern> map = new HashMap<>();
            for (BugPattern bugPattern : bugPatterns) {
                map.putIfAbsent(bugPattern.getIdentifier(), bugPattern);
            }
            Collection<BugPattern> bugPatternsUniqueInstances = map.values();


            StringBuilder bugSummaryTable = new StringBuilder();
            AtomicInteger counter = new AtomicInteger(0);
            bugPatternsUniqueInstances.forEach(bug -> {
                String evenOdd = (counter.get() % 2 == 0) ? "1" : "0";
                bugSummaryTable.append("<tr class=\"tablerow"+evenOdd+"\"><td><a href=\"#"+bug.getIdentifier()+"\">"
                        +bug.getIdentifier()+": "+bug.getDescription()+"</a></td><td align=\"right\">" +
                        bugPatterns.stream().filter(bug.getClass()::isInstance).count() +
                        "</td></tr>");
                counter.getAndIncrement();
            });

            StringBuilder bugsPatternsTable = new StringBuilder();
            AtomicInteger counter2 = new AtomicInteger(0);
            bugPatterns.forEach(bug -> {
                String evenOdd = (counter2.get() % 2 == 0) ? "1" : "0";
                bugsPatternsTable.append("<tr onclick=\"toggleRow('row"+counter2.get()+"');\" class=\"tablerow"+evenOdd+"\">\n" +
                        "                    <td>\n" +
                        "                    <span>"+bug.getIdentifier()+"</span>\n" +
                        "                    </td>\n" +
                        "                    <td>"+bug.getPath()+": "+bug.getDescription()+"</td>\n" +
                        "                </tr>\n" +
                        "                <tr class=\"detailrow"+evenOdd+"\">\n" +
                        "                    <td>\n" +
                        "                        </td><td>\n" +
                        "                        <p style=\"display: none;\" id=\"row"+counter2.get()+"\">\n" +
                        "                            <a href=\"#"+bug.getIdentifier()+"\">Bug type "+bug.getName()+" (click for details)</a>\n" +
                        "                            <br>In file "+bug.getFile()+"<br>In method \""+bug.getFunctionName()+"\"<br>At line "+bug.getLine()+"\n" +
                        "                        </p>\n" +
                        "                    </td>\n" +
                        "                </tr>");
                counter2.getAndIncrement();
            });

            htmlString = htmlString.replace("$path", path.getAbsolutePath());
            htmlString = htmlString.replace("$bugsSummmaryTable", bugSummaryTable);
            htmlString = htmlString.replace("$totalNumberOfBugs", String.valueOf(bugPatterns.size()));
            htmlString = htmlString.replace("$bugPatternsTable", bugsPatternsTable);

            writeInFile(htmlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MethodDeclaration getMethodDeclaration(Node node) {
        Node currentParent = node.getParentNode().orElse(null);
        while (!(currentParent instanceof MethodDeclaration) && currentParent != null) {
            currentParent = currentParent.getParentNode().orElse(null);
        }

        MethodDeclaration methodDeclaration = (MethodDeclaration) currentParent;

        if (methodDeclaration == null) {
            return null;
        }

        return methodDeclaration;
    }

    public static String getFunctionName(Node node) {
        MethodDeclaration methodDeclaration = Util.getMethodDeclaration(node);

        if (methodDeclaration == null) {
            return null;
        }

        return Objects.requireNonNull(methodDeclaration).getName().getIdentifier();
    }

    public static String getClassName(Node node) {
        Node currentParent = node.getParentNode().orElse(null);
        while (!(currentParent instanceof ClassOrInterfaceDeclaration) && currentParent != null) {
            currentParent = currentParent.getParentNode().orElse(null);
        }

        ClassOrInterfaceDeclaration className = (ClassOrInterfaceDeclaration) currentParent;

        if (className == null) {
            return null;
        }

        return Objects.requireNonNull(className).getName().getIdentifier();
    }

    public static int getLineNumber(Node statement) {
        return statement.getRange().isPresent() ? statement.getRange().get().begin.line : 0;
    }
}
