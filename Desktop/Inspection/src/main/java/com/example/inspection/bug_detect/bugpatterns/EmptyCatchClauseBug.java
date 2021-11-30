package com.example.inspection.bug_detect.bugpatterns;

import java.io.File;

public class EmptyCatchClauseBug extends BugPattern {
    public EmptyCatchClauseBug(int line, File file, String functionName) {
        super(line, file, functionName);
    }

    @Override
    public String getIdentifier() {
        return "EC";
    }

    @Override
    public String getName() {
        return "Empty Catch Clause";
    }

    @Override
    public String getDescription() {
        return "Empty Catch block found";
    }
}