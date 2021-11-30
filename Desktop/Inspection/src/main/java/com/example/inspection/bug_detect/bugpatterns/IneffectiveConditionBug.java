package com.example.inspection.bug_detect.bugpatterns;

import java.io.File;

public class IneffectiveConditionBug extends BugPattern {

	public IneffectiveConditionBug(int line, File file, String functionName) {
		super(line, file, functionName);
	}

	@Override
	public String getIdentifier() {
		return "IC";
	}

	@Override
	public String getName() {
		return "Ineffective condition";
	}

	@Override
	public String getDescription() {
		return "Condition is ineffective";
	}

}
