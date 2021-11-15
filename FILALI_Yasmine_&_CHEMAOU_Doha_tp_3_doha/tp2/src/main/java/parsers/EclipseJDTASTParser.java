package parsers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class EclipseJDTASTParser extends Parser<ASTParser> {

	/* CONSTRUCTOR */
	public EclipseJDTASTParser(String projectPath) {
		super(projectPath);
	}

	/* METHODS */
	// paraméter le parseur
	public void setParser(int level, int kind, boolean resolveBindings, boolean bindingsRecovery, String encoding) {
		parser = ASTParser.newParser(level);
		parser.setKind(kind);
		parser.setResolveBindings(resolveBindings);
		parser.setBindingsRecovery(bindingsRecovery);
		parser.setEnvironment(new String[] { getJREPath() }, new String[] { getProjectPath() },
				new String[] { encoding }, true);
		parser.setCompilerOptions(JavaCore.getOptions());
		parser.setUnitName("");
	}

	// récupérer un cu du fichier
	public CompilationUnit parse(File sourceFile) throws IOException {
		Charset platformCharset = null;
		parser.setSource(FileUtils.readFileToString(sourceFile, platformCharset).toCharArray());

		return (CompilationUnit) parser.createAST(null);
	}

	// récupérer les cu (noeud root de l'ast) de tous les fichiers
	public List<CompilationUnit> parseProject() throws IOException {

		List<CompilationUnit> cUnits = new ArrayList<>();

		for (File sourceFile : listJavaProjectFiles()) {
			/*
			 * Le bout de code qui permet de garder la configuration de l'ASTParser
			 * cohérente et de construire les models pour resolve les bindings!!!!
			 */
			// System.out.println("*****************************");
			// System.out.println(sourceFile);
			configure();
			CompilationUnit cUnit = parse(sourceFile);
			cUnits.add(cUnit);
		}
		return cUnits;
	}

	@Override
	public void configure() {
		setParser(AST.JLS8, ASTParser.K_COMPILATION_UNIT, true, true, "UTF-8");
	}
}
