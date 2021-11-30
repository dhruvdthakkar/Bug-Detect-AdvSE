package com.example.inspection.bug_detect.checkers;

import com.example.inspection.bug_detect.DirExplorer;
import com.example.inspection.bug_detect.Util;
import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.example.inspection.bug_detect.bugpatterns.StringComparisonBug;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StringComparisonChecker implements Checker {
    public List<BugPattern> check(File projectDir) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        Map<String, Map<Integer, Set<String>>> variables = new HashMap<>();

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);

                        NodeList<Parameter> nodes = n.getParameters();

                        for (Parameter parameter : nodes) {
                            if (parameter.getTypeAsString().equals("String")) {
                                String className = Util.getClassName(n);
                                int functionHashcode = n.hashCode();

                                if (className != null) {
                                    if (variables.containsKey(className)) {
                                        Map<Integer, Set<String>> mapClass = variables.get(className);
                                        if (mapClass.containsKey(functionHashcode)) {
                                            mapClass.get(functionHashcode).add(parameter.getNameAsString());
                                        } else {
                                            Set<String> methodSet = new HashSet<>();
                                            methodSet.add(parameter.getNameAsString());
                                            mapClass.put(functionHashcode, methodSet);
                                        }
                                    } else {
                                        Map<Integer, Set<String>> mapClass = new HashMap<>();
                                        Set<String> methodSet = new HashSet<>();
                                        methodSet.add(parameter.getNameAsString());
                                        mapClass.put(functionHashcode, methodSet);
                                        variables.put(className, mapClass);
                                    }
                                }
                            }
                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ExpressionStmt n, Object arg) {
                        super.visit(n, arg);
                        String stmt = n.toString();
                        if (stmt.contains("=") && stmt.substring(0, stmt.indexOf("=")).contains("String")) {
                            int loc = stmt.indexOf("String") + 6;
                            int eloc = stmt.indexOf("=");

                            String tmpAdd = stmt.substring(loc, eloc);
                            tmpAdd = tmpAdd.replaceAll("\\s+", "");

                            String className = Util.getClassName(n);
                            MethodDeclaration methodDeclaration = Util.getMethodDeclaration(n);

                            if (className != null && methodDeclaration != null) {
                                int functionHashcode = methodDeclaration.hashCode();
                                if (variables.containsKey(className)) {
                                    Map<Integer, Set<String>> mapClass = variables.get(className);
                                    if (mapClass.containsKey(functionHashcode)) {
                                        mapClass.get(functionHashcode).add(tmpAdd);
                                    } else {
                                        Set<String> methodSet = new HashSet<>();
                                        methodSet.add(tmpAdd);
                                        mapClass.put(functionHashcode, methodSet);
                                    }
                                } else {
                                    Map<Integer, Set<String>> mapClass = new HashMap<>();
                                    Set<String> methodSet = new HashSet<>();
                                    methodSet.add(tmpAdd);
                                    mapClass.put(functionHashcode, methodSet);
                                    variables.put(className, mapClass);
                                }
                            }
                        }
                    }

                    @Override
                    public void visit(IfStmt n, Object arg) {
                        super.visit(n, arg);
                        Node nCond = n.getChildNodes().get(0);
                        String className = Util.getClassName(n);
                        String functionName = Util.getFunctionName(n);
                        MethodDeclaration methodDeclaration = Util.getMethodDeclaration(n);
                        if (methodDeclaration != null) {
                            int functionHashcode = methodDeclaration.hashCode();

                            if (nCond != null &&
                                    className != null &&
                                    functionName != null &&
                                    variables.containsKey(className) &&
                                    variables.get(className).containsKey(functionHashcode) &&
                                    shouldAddBugPattern(nCond, variables.get(className).get(functionHashcode))) {
                                int line = (n.getRange().isPresent() ? n.getRange().get().begin.line : 0);

                                bugPatterns.add(new StringComparisonBug(line, file, functionName));
                            }
                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);

    	return bugPatterns;
    }

    private static boolean shouldAddBugPattern(Node n, Set<String> variables)
    {
        if (n instanceof BinaryExpr) {
            boolean checkVariable = false;

            if (((BinaryExpr) n).getOperator().toString().equals("EQUALS")) {
                checkVariable = true;
            }
            else if (((BinaryExpr) n).getOperator().toString().equals("NOT_EQUALS")) {
                checkVariable = true;
            }
            
            if(((BinaryExpr) n).getLeft().toString().equals("null")) {
            	checkVariable = false;
            }
            if(((BinaryExpr) n).getRight().toString().equals("null")) {
            	checkVariable = false;
            }
              if((((BinaryExpr) n).getRight() instanceof IntegerLiteralExpr)||(((BinaryExpr) n).getRight() instanceof DoubleLiteralExpr)) {	
            	checkVariable = false;
            }
            
			if((((BinaryExpr) n).getLeft() instanceof IntegerLiteralExpr)||(((BinaryExpr) n).getLeft() instanceof DoubleLiteralExpr)) {          	
				checkVariable = false;
			 }

            if (checkVariable) {
                for (Node node : n.getChildNodes()) {
                    if (node.getClass().getSimpleName().equals("NameExpr")) {
                        String variableToCheck = node.toString();
                        if (variables.contains(variableToCheck)) {
                            return true;
                        }
                    }
                    return shouldAddBugPattern(node, variables);
                }
            }
        }

        for (Node node : n.getChildNodes()) {
            if (!shouldAddBugPattern(node, variables)) {
                return false;
            }
        }

        return false;
    }
}
