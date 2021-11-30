package com.example.inspection.bug_detect.bugpatterns;
import java.io.File;

public class UnneededComputationInLoopsBug extends BugPattern {

	public UnneededComputationInLoopsBug(int line, File file, String functionName) {
		super(line, file, functionName);
	}

	 @Override
	    public String getIdentifier() {
	        return "UC";
	    }

	    @Override
	    public String getName() {
	        return "Uneeded computation in loops";
	    }

	    @Override
	    public String getDescription() {
	        return "Uneeded computation is present in a loop";
	    }

}
