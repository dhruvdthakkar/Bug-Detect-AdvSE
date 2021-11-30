package com.example.inspection.bug_detect.bugpatterns;
import java.io.File;

public class StringComparisonBug extends BugPattern {
    public StringComparisonBug(int line, File file, String functionName) {
        super(line, file, functionName);
    }

    @Override
    public String getIdentifier() {
        return "ES";
    }

    @Override
    public String getName() {
        return "String Comparison";
    }

    @Override
    public String getDescription() {
        return "Comparison of String objects using == or !=";
    }
}
