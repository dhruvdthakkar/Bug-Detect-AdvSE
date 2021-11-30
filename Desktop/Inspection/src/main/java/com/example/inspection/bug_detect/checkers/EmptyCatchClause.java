package com.example.inspection.bug_detect.checkers;


import com.example.inspection.bug_detect.bugpatterns.EmptyCatchClauseBug;
import com.example.inspection.bug_detect.DirExplorer;
import com.example.inspection.bug_detect.Util;
import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmptyCatchClause implements Checker {
    public List<BugPattern> check(File projectDir) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(CatchClause n, Object arg) {
                       super.visit(n, arg);

                       BlockStmt blockStatement = n.getBody();
                       NodeList<Statement> statements = blockStatement.getStatements();

                       if(statements.isEmpty()) {
                           int lineNumber = Util.getLineNumber(blockStatement);

                           String functionName = Util.getFunctionName(n);

                           bugPatterns.add(new EmptyCatchClauseBug(lineNumber, file, functionName));
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
