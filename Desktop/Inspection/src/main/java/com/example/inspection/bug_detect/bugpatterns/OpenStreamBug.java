package com.example.inspection.bug_detect.bugpatterns;

import java.io.File;

public class OpenStreamBug extends BugPattern {
    public OpenStreamBug(int line, File file, String functionName) {
        super(line, file, functionName);
    }

    @Override
    public String getIdentifier() {
        return "OS";
    }

    @Override
    public String getName() {
        return "Open Stream";
    }

    @Override
    public String getDescription() {
        return "Method may fail to close stream";
    }
}
