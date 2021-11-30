package com.example.inspection.bug_detect.checkers;

import com.example.inspection.bug_detect.DirExplorer;
import com.example.inspection.bug_detect.Util;
import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.example.inspection.bug_detect.bugpatterns.OvercatchExceptionTerminationBug;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OvercatchExceptionTerminationChecker implements Checker {
    public List<BugPattern> check(File projectDir) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(CatchClause n, Object arg) {
                        super.visit(n, arg);

                        Parameter parameter = n.getParameter();
                        if (parameter.getType().asString().matches("Exception|ClassNotFoundException|" +
                                "ClassNotSupportedException|IllegalAccessException|" +
                                "InstantiationException|InterruptedException|" +
                                "NoSuchFieldException|NoSuchMethodException|" +
                                "RuntimeException")) {

                            BlockStmt blockStatement = n.getBody();
                            NodeList<Statement> statements = blockStatement.getStatements();

                            for (Statement s: statements) {

                                if (s.isExpressionStmt()) {
                                    ExpressionStmt expressionStmt = s.asExpressionStmt();
                                    Expression expression = expressionStmt.getExpression();

                                    if (expression.isMethodCallExpr()) {

                                        MethodCallExpr methodCallExpr = expression.asMethodCallExpr();

                                        if (methodCallExpr.getScope().isPresent() &&
                                                methodCallExpr.getScope().get().isNameExpr()) {

                                            NameExpr nameExpr = methodCallExpr.getScope().get().asNameExpr();

                                            if (nameExpr.getName().getIdentifier().equals("System") &&
                                                    methodCallExpr.getName().getIdentifier().equals("exit")) {

                                                String functionName = Util.getFunctionName(n);

                                                int lineNumber = Util.getLineNumber(s);

                                                bugPatterns.add(new OvercatchExceptionTerminationBug(lineNumber, file, functionName));
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

        return bugPatterns;
     }
}
