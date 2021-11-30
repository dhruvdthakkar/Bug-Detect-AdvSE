package com.example.inspection.bug_detect.bugpatterns;

import java.io.File;

public class OvercatchExceptionTerminationBug extends BugPattern {
    public OvercatchExceptionTerminationBug(int line, File file, String functionName) {
        super(line, file, functionName);
    }

    @Override
    public String getIdentifier() {
        return "OC";
    }

    @Override
    public String getName() {
        return "Overcatch Exception Termination";
    }

    @Override
    public String getDescription() {
        return "Over-catching an exception with system-termination";
    }
}
