package com.example.inspection.bug_detect.bugpatterns;

import java.io.File;

public class EqualsHashcodeBug extends BugPattern {
    public EqualsHashcodeBug(int line, File file, String functionName) {
        super(line, file, functionName);
    }

    @Override
    public String getIdentifier() {
        return "HE";
    }

    @Override
    public String getName() {
        return "Equals Hashcode";
    }

    @Override
    public String getDescription() {
        return "Class defines equals() but not hashCode()";
    }
}
