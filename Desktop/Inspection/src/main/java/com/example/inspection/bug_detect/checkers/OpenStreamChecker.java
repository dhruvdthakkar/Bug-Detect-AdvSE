package com.example.inspection.bug_detect.checkers;

import com.example.inspection.bug_detect.DirExplorer;
import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.example.inspection.bug_detect.bugpatterns.OpenStreamBug;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OpenStreamChecker implements Checker {

    private final String streamClasses = "InputStream|OutputStream" +
            "ByteArrayInputStream|ByteArrayOutputStream|" +
            "StringReader|StringWriter|" +
            "ServletRequest|ServletResponse|" +
            "FileInputStream|FileOutputStream|" +
            "ZipFile|Reader|Writer|Connection|" +
            "Statement|ResultSet|Socket";


    @Override
    public List<BugPattern> check(File projectDir) {
        HashMap<String, BugPattern> streams = new HashMap<>();

        List<BugPattern> bugPatterns = new ArrayList<>();

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(MethodDeclaration n, Object arg) {
                        super.visit(n, arg);

                        if (n.getBody().isPresent()) {
                            BlockStmt blockStatement = n.getBody().get();

                            for (TryStmt s: blockStatement.findAll(TryStmt.class)) {

                                    for (ExpressionStmt tryStatement: s.getTryBlock().findAll(ExpressionStmt.class)) {

                                            if (tryStatement.getExpression().isAssignExpr()) {

                                                AssignExpr assignExpr = (AssignExpr) tryStatement.getExpression();
                                                Expression value = assignExpr.getValue();

                                                if (assignExpr.getTarget().isNameExpr()) {
                                                    String streamName = ((NameExpr) assignExpr.getTarget()).getName().getIdentifier();

                                                    if (value.isObjectCreationExpr()) {

                                                        ClassOrInterfaceType type = ((ObjectCreationExpr) value).getType();

                                                        if (type.getName().getIdentifier().matches(streamClasses)) {

                                                            int line = (assignExpr.getRange().isPresent() ? assignExpr.getRange().get().begin.line : 0);
                                                            streams.put(streamName, new OpenStreamBug(line, file, n.getNameAsString()));
                                                        }
                                                    }
                                                }
                                            }

                                            else if (tryStatement.getExpression().isVariableDeclarationExpr()) {

                                                NodeList<VariableDeclarator> variables = ((VariableDeclarationExpr) tryStatement.getExpression()).getVariables();
                                                variables.forEach(variable -> {
                                                    if (variable.getType().isClassOrInterfaceType()) {
                                                        String streamName = variable.getName().getIdentifier();
                                                        ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) variable.getType();
                                                        String streamType = classOrInterfaceType.getName().getIdentifier();

                                                        if (streamType.matches(streamClasses)) {
                                                            int line = (variable.getRange().isPresent() ? variable.getRange().get().begin.line : 0);
                                                            streams.put(streamName, new OpenStreamBug(line, file, n.getNameAsString()));
                                                        }
                                                    }
                                                });
                                            }
                                    }

                                    if (s.getFinallyBlock().isPresent()) {
                                        for (ExpressionStmt expressionStmt: s.getFinallyBlock().get().findAll(ExpressionStmt.class)) {

                                            if (expressionStmt.getExpression().isMethodCallExpr()) {
                                                MethodCallExpr methodCallExpr = (MethodCallExpr) expressionStmt.getExpression();

                                                if (methodCallExpr.getScope().isPresent() &&  methodCallExpr.getScope().get().isNameExpr()) {
                                                    NameExpr nameExpr = (NameExpr) methodCallExpr.getScope().get();
                                                    String streamName = nameExpr.getName().getIdentifier();

                                                    if (methodCallExpr.getName().getIdentifier().equals("close")) {
                                                        streams.remove(streamName);
                                                    }

                                                }
                                            }
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


        streams.forEach((streamName, bugPattern) -> bugPatterns.add(bugPattern));

        return bugPatterns;
    }
}
