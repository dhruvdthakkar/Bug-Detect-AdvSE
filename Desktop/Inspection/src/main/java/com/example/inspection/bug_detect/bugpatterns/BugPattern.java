package com.example.inspection.bug_detect.bugpatterns;

import java.io.File;
import java.util.Objects;

public abstract class BugPattern implements Bug {
    private int line;

    private File file;

    private String functionName;

    BugPattern(int line, File file, String functionName) {
        this.line = line;
        this.file = file;
        this.functionName = functionName;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return file.getPath();
    }

    public String getFilename() {
        return file.getName();
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BugPattern that = (BugPattern) o;
        return line == that.line &&
                Objects.equals(file, that.file) &&
                Objects.equals(functionName, that.functionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, file, functionName);
    }

    @Override
    public String toString() {
        return "\nBugPattern{" +
                "identifier='" + getIdentifier() + '\'' +
                ", fileName='" + getFilename() + '\'' +
                ", functionName='" + functionName + '\'' +
                ", line=" + line +
                ", path='" + getPath() + '\'' +
                "}";
    }
}
