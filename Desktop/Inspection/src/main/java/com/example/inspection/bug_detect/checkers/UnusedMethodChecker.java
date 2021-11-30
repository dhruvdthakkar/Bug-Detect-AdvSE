package com.example.inspection.bug_detect.checkers;

import com.example.inspection.bug_detect.DirExplorer;
import com.example.inspection.bug_detect.Util;
import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.example.inspection.bug_detect.bugpatterns.UnusedMethodBug;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UnusedMethodChecker implements Checker {

    @Override
    public List<BugPattern> check(File projectDir) {
        List<BugPattern> bugPatterns = new ArrayList<>();
        Map<String, Set<String>> methodCallsInClass = new HashMap<>();

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {

                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodCallExpr n, Object arg) {
                        super.visit(n, arg);

                        Node currentParent = n.getParentNode().orElse(null);
                        while (!(currentParent instanceof ClassOrInterfaceDeclaration) && currentParent != null) {
                            currentParent = currentParent.getParentNode().orElse(null);
                        }

                        if (currentParent != null) {
                            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) currentParent;
                            CompilationUnit compilationUnit = (CompilationUnit) currentParent.getParentNode().orElse(null);
                            if (compilationUnit != null) {

                                List<Node> imports = compilationUnit.getChildNodes().stream().filter(ImportDeclaration.class::isInstance).collect(Collectors.toList());
                                boolean jUnit = false;
                                for (Node i : imports) {
                                    String importString = ((ImportDeclaration) i).getNameAsString();
                                    if (importString.startsWith("org.junit")) {
                                        jUnit = true;
                                        break;
                                    }
                                }
                                if (!jUnit) {
                                    String className = classOrInterfaceDeclaration.getNameAsString();

                                    if (methodCallsInClass.containsKey(className)) {
                                        methodCallsInClass.get(className).add(n.getNameAsString());
                                    } else {
                                        Set<String> methodSet = new HashSet<>();
                                        methodSet.add(n.getNameAsString());
                                        methodCallsInClass.put(className, methodSet);
                                    }
                                }
                            }

                        }
                    }
                }.visit(JavaParser.parse(file), null);

            } catch (RuntimeException | IOException ignored) {

            }
        }).explore(projectDir);

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);

                        String className = n.getNameAsString();
                        if (methodCallsInClass.containsKey(className)) {
                            Set<String> classMethods = methodCallsInClass.get(className);

                            for (MethodDeclaration method : n.getMethods()) {
                                String currentClassMethodName = method.getNameAsString();
                                boolean found = false;
                                for (String methodName : classMethods) {
                                    if (methodName.equals(currentClassMethodName)) {
                                        found = true;
                                        break;
                                    }
                                }

                                if (!found && !currentClassMethodName.equals("main")) {
                                    bugPatterns.add(new UnusedMethodBug(Util.getLineNumber(method), file, currentClassMethodName));
                                }
                            }
                        }
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (RuntimeException | IOException ignored) {
            }
        }).explore(projectDir);

        return bugPatterns;
    }
}
