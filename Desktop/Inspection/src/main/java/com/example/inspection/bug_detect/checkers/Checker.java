package com.example.inspection.bug_detect.checkers;

import com.example.inspection.bug_detect.bugpatterns.BugPattern;

import java.io.File;
import java.util.List;

public interface Checker {
    List<BugPattern> check(File projectDir);
}
