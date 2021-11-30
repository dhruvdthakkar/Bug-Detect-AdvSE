package com.example.inspection.bug_detect.checkers;

import com.example.inspection.bug_detect.Util;
import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.example.inspection.bug_detect.bugpatterns.TodoFixMeCatchBlockBug;
import com.example.inspection.bug_detect.DirExplorer;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TodoFixMeCatchBlockChecker implements Checker {
    public List<BugPattern> check(File projectDir) {
        List<BugPattern> bugPatterns = new ArrayList<>();

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(CatchClause n, Object arg) {
                       super.visit(n, arg);
                       for (Comment comment : n.getAllContainedComments()) {
                           if (comment.getContent().toLowerCase().contains("fixme") || comment.getContent().toLowerCase().contains("todo")) {

                               int lineNumber = Util.getLineNumber(comment);

                               String functionName = Util.getFunctionName(n);

                               bugPatterns.add(new TodoFixMeCatchBlockBug(lineNumber, file, functionName));
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
