package com.example.inspection.bug_detect;



import com.example.inspection.bug_detect.checkers.*;
import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.example.inspection.bug_detect.checkers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Main {
//    public static void main(String[] args) {
//    	initSetup();
//    }

    public static void initSetup()
    {
        Util.deleteFiles();

        File projectDir = new File("C:\\Users\\Dhruv Thakkar\\Desktop\\Inspection\\src\\main\\resources\\static\\fileforparse.java");
    
        Set<BugPattern> bugPatterns = new HashSet<>();
        Checker checker = new EqualsHashcodeChecker();
        bugPatterns.addAll(checker.check(projectDir));
        checker = new TodoFixMeCatchBlockChecker();
        bugPatterns.addAll(checker.check(projectDir));

        checker = new StringComparisonChecker();
        bugPatterns.addAll(checker.check(projectDir));

        checker = new OvercatchExceptionTerminationChecker();
        bugPatterns.addAll(checker.check(projectDir));

        checker = new OpenStreamChecker();
        bugPatterns.addAll(checker.check(projectDir));
        
        checker = new EmptyCatchClause();
        bugPatterns.addAll(checker.check(projectDir));
        
        checker = new DuplicateLoggingStatementInCatchBlockOfSameTry();
        bugPatterns.addAll(checker.check(projectDir));
        
        checker = new UnneededComputationInLoopsChecker();
        bugPatterns.addAll(checker.check(projectDir));

        checker = new UnusedMethodChecker();
        bugPatterns.addAll(checker.check(projectDir));

        checker = new IneffectiveConditionChecker();
        bugPatterns.addAll(checker.check(projectDir));

        System.out.println("Hit");
        System.out.println(bugPatterns);

        Util.generateReport(projectDir, new ArrayList(bugPatterns));
    }
}
