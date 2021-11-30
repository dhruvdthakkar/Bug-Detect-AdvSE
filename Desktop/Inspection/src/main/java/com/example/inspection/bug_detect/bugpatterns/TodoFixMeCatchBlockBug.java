package com.example.inspection.bug_detect.bugpatterns;

import java.io.File;

public class TodoFixMeCatchBlockBug extends BugPattern {
    public TodoFixMeCatchBlockBug(int line, File file, String functionName) {
        super(line, file, functionName);
    }

    @Override
    public String getIdentifier() {
        return "UF";
    }

    @Override
    public String getName() {
        return "Todo FixMe Catch Block";
    }

    @Override
    public String getDescription() {
        return "Unfinished exception handling code";
    }
}
