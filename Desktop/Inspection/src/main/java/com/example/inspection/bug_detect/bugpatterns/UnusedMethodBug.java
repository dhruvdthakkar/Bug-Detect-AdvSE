package com.example.inspection.bug_detect.bugpatterns;

import java.io.File;

public class UnusedMethodBug extends BugPattern {

	public UnusedMethodBug(int line, File file, String functionName) {
		super(line, file, functionName);
	}

	@Override
	public String getIdentifier() {
		return "UM";
	}

	@Override
	public String getName() {
		return "Unused method";
	}

	@Override
	public String getDescription() {
		return "The method is defined but never used";
	}

}
