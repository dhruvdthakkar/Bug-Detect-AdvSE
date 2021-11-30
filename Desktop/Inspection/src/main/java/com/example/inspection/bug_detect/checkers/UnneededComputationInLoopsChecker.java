package com.example.inspection.bug_detect.checkers;

import com.example.inspection.bug_detect.Util;
import com.example.inspection.bug_detect.bugpatterns.BugPattern;
import com.example.inspection.bug_detect.bugpatterns.UnneededComputationInLoopsBug;
import com.example.inspection.bug_detect.DirExplorer;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnneededComputationInLoopsChecker implements Checker {
	private ArrayList<String> variables = new ArrayList<>();
	private ArrayList<Node> variableNodes = new ArrayList<>();
	File file;

	@Override
	public List<BugPattern> check(File projectDir) {
		List<BugPattern> bugPatterns = new ArrayList<>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
        	this.file = file;
            try {
            	new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ForStmt forStmt, Object arg) {
                        super.visit(forStmt, arg);
                        visitFor(forStmt, bugPatterns);
                    }
                }.visit(JavaParser.parse(file), null);
            	
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ForeachStmt forEachStmt, Object arg) {
                        super.visit(forEachStmt, arg);
                        visitForEach(forEachStmt, bugPatterns);
                    }
                }.visit(JavaParser.parse(file), null);
                
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(WhileStmt whileStmt, Object arg) {
                        super.visit(whileStmt, arg);
                        visitWhile(whileStmt, bugPatterns);
                    }
                }.visit(JavaParser.parse(file), null);
                
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(DoStmt doWhileStmt, Object arg) {
                        super.visit(doWhileStmt, arg);
                        visitDoWhile(doWhileStmt, bugPatterns);
                    }
                }.visit(JavaParser.parse(file), null);
                
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
		
		return bugPatterns;
	}

	private void visitDoWhile(DoStmt doWhileStmt, List<BugPattern> bugPatterns) {
		if (doWhileStmt.getBody().isBlockStmt()) {
			inspectBody((BlockStmt) doWhileStmt.getBody(), bugPatterns);
		}
		variables.clear();
		variableNodes.clear();
	}

	private void visitWhile(WhileStmt whileStmt, List<BugPattern> bugPatterns) {
		if (whileStmt.getBody().isBlockStmt()) {
			inspectBody((BlockStmt) whileStmt.getBody(), bugPatterns);
		}
		variables.clear();
		variableNodes.clear();
	}

	private void visitFor(ForStmt forStmt, List<BugPattern> bugPatterns) {
		try {
			Node iteratorDeclaration = forStmt.getInitialization().get(0);
				Node iterator = iteratorDeclaration.getChildNodes().get(0).getChildNodes().get(1);
				addVariable(bugPatterns, iterator, forStmt.getBody());
		} catch (IndexOutOfBoundsException ignored) {

		}
	}

	private void addVariable(List<BugPattern> bugPatterns, Node iterator, Statement body) {
		variables.add(iterator.toString());
		variableNodes.add(iterator);
		if (body.isBlockStmt()) {
			inspectBody((BlockStmt) body, bugPatterns);
		}
		variables.clear();
		variableNodes.clear();
	}

	private void visitForEach(ForeachStmt foreachStmt, List<BugPattern> bugPatterns) {
		try {
			Node iterator = foreachStmt.getVariable().getChildNodes().get(0);
			if (iterator != null) {
				addVariable(bugPatterns, iterator, foreachStmt.getBody());
			}
		} catch (IndexOutOfBoundsException ignored) {

		}
	}

	private void inspectBody(BlockStmt body, List<BugPattern> bugPatterns) {
		NodeList<Statement> lines = body.getStatements();
		
		for(Statement line : lines) {
			
			if (line.isExpressionStmt()) {
				try {
					Node expression = line.getChildNodes().get(0).getChildNodes().get(0);
					String declarationClass = "class com.github.javaparser.ast.expr.VariableDeclarationExpr";
					boolean declarationStmt = line.getChildNodes().get(0).getClass().toString().equals(declarationClass);

					if (declarationStmt) {
						try {
							Node leftVar = expression.getChildNodes().get(1);
							variables.add(leftVar.toString());
							variableNodes.add(leftVar);
						} catch (IndexOutOfBoundsException ignored) {

						}
					}

					if (declarationStmt && expression.getChildNodes().size() > 1) {
						try {
							Node rightExpression = expression.getChildNodes().get(2);
							removeVariables(rightExpression);
						} catch (IndexOutOfBoundsException ignored) {

						}
					}
					else if (line.getChildNodes().get(0).getChildNodes().size() > 0) {
						try {
							Node rightExpression = line.getChildNodes().get(0).getChildNodes().get(1);
							removeVariables(rightExpression);
						} catch (IndexOutOfBoundsException ignored) {

						}
					} 
				} catch (IndexOutOfBoundsException ignored) {

				}
			}

			List<Node> instructions = (List<Node>) line.getChildNodes();
			findVariables(instructions);
		}
				
		if (variableNodes.size() > 0) {
			for(Node n : variableNodes) {
				String functionName = Util.getFunctionName(n);
	            int lineNumber = Util.getLineNumber(n);
	            
	            bugPatterns.add(new UnneededComputationInLoopsBug(lineNumber, file, functionName));
			}            
		}
	}

	private void findVariables(List<Node> instructions) {
		if(instructions == null || instructions.size() == 0) return;
		
		if(instructions.size() == 0) {
			return;
		}
		
		for(Node n : instructions) {
			findVariables((List<Node>) n.getChildNodes());
			if(variables.contains(n.toString())) {
				removeVariables(n);
			}
		}		
	}

	private void removeVariables(Node rightExpression) {
		for(Node rightVar : rightExpression.getChildNodes()) {
			if (variables.contains(rightVar.toString())) {
				int index = variables.indexOf(rightVar.toString());
				variableNodes.remove(index);
				variables.remove(rightVar.toString());
			}
		}
	}
}
