package flakes;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import flakyhoover.AbstractFlaky;
import flakyhoover.AbstractFlakyElement;
import flakyhoover.MetaData;
import flakyhoover.TestMethod;
import util.TestSmell;

public class FireAndForget extends AbstractFlaky {
	private List<AbstractFlakyElement> flakyElementList;


	public FireAndForget() {
		flakyElementList = new ArrayList<>();
	}

	@Override
	public boolean getHasFlaky() {
		return flakyElementList.stream().filter(x -> x.getHasFlaky()).count() >= 1;
	}

	@Override
	public String getFlakyName() {
		return "FireAndForget";
	}

	@Override
	public void runAnalysis(CompilationUnit testFileCompilationUnit, CompilationUnit productionFileCompilationUnit, 
			String testClassName, String projectName) throws FileNotFoundException {
		FireAndForget.ClassVisitor classVisitor;
		classVisitor = new FireAndForget.ClassVisitor();
		classVisitor.visit(testFileCompilationUnit, null);
	}

	@Override
	public List<AbstractFlakyElement> getFlakyElements() {
		return flakyElementList;
	}

	private class ClassVisitor extends VoidVisitorAdapter<Void> {
		private MethodDeclaration currentMethod = null;
		private boolean hasFlaky = false;
		TestMethod testMethod;
		private List<MetaData> metaData = new ArrayList<MetaData>();
		private List<String> asyncWords = Arrays.asList("wait", "await", "async", "asynchronous", "sleep","receive", "Thread.sleep","waiting", "timeout");
		
		private List<String> externalCalls = Arrays.asList("server", "client", "context", "database",
														"soap", "call", "getResource","manager", "factory", 
														"response", "cursor", "FileOutputStream", "sql", "read",
														"http", "fetch", "receive", "connect", "obtain", "db", 
														"aquire", "create","execute", "getInstance", "load");
		private int lineNrSync = -1;
		private int lineNrExternal = -1;

		private boolean containerContain(List<String> container, String keyword) {
			for(int i = 0; i < container.size(); i++) {
				if(keyword.contains(container.get(i))) {
					System.out.println("containerContain: " + container.get(i).toString());
					return true;
				}
			}
			return false;
		}
		
		
		// examine all methods in the test class
		@Override
		public void visit(MethodDeclaration n, Void arg) {
			System.out.println("MethodDeclaration: " + n.getNameAsString());
			currentMethod = n;
			
			testMethod = new TestMethod(n.getNameAsString(), n.getBegin().get().line);
			testMethod.setHasFlaky(false); //default value is false (i.e. no flakiness)

			super.visit(n, arg);
			
			if(this.lineNrSync == -1 && this.lineNrExternal != -1 ) {
				this.hasFlaky = true;
			}
			else if(this.lineNrExternal > this.lineNrSync) {
				System.out.println("!2");
				this.hasFlaky = true;
			}else {
				System.out.println("!3");
				this.hasFlaky = false;
			}

			testMethod.setHasFlaky(hasFlaky==true);
			testMethod.addMetaDataItem("VariableCond", metaData);

			if(testMethod.getHasFlaky()) {
				flakyElementList.add(testMethod);				
			}

			//reset values for next method
			currentMethod = null;
			hasFlaky = false;
			this.lineNrExternal = -1;
			this.lineNrSync = -1;
		}
		
		@Override
		public void visit(ExpressionStmt n, Void arg) {
			if(currentMethod != null) {
				if(containerContain(externalCalls, n.toString())){
					this.lineNrExternal = n.getBegin().get().line;
					metaData.add(new MetaData(n.getBegin().get().line, n.getClass().getSimpleName(), n.toString(), true));
				}
				if(containerContain(asyncWords, n.toString())) {
					this.lineNrSync= n.getBegin().get().line;
					if(metaData.size() > 0)
						metaData.get(metaData.size()-1).setFlaky(false);
				}
			}
			super.visit(n, arg);
		}
	}

	@Override
	public ArrayList<TestSmell> getTestSmells() {
		// TODO Auto-generated method stub
		return null;
	}
}
