package packagesANDclasses;

/** Auteurs : 
*	@author Yasmine FILALI 
*	@author Doha CHEMAOU
*/

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ASTVisitor;

import parsers.EclipseJDTASTParser;
import main.CallGraphMain;



public class Package_Class {
    static List<String> packages_classes = new ArrayList<>();
    static String package_class="";
    public static List<String> classes = new ArrayList<>();
    public static List<String> classes_with_lower_cases = new ArrayList<>();

    public static List<String> list_of_package_class() throws IOException{
        String test_project_path = CallGraphMain.TEST_PROJECT_PATH;
        EclipseJDTASTParser eJDTASTParser = new EclipseJDTASTParser(test_project_path);
        List<File> projectFiles = eJDTASTParser.listJavaProjectFiles();

        for (File class_path : projectFiles){
            String class_path_ = class_path.toString();
            // split the filePath to get the name of the class
            String[] s;
            if(class_path_.contains("\\")){
                s = class_path_.split("\\\\");
            }
            else {
                s = class_path_.split("/");
            }
            String class_ = s[s.length-1];
            if(!classes.contains(class_))
                classes.add(class_);
                String class__ = class_.split("[.]")[0];
                class__ = class__.toLowerCase();
            if(!classes_with_lower_cases.contains(class__)){
                classes_with_lower_cases.add(class__);
            }
                
            package_class= class_;
            s = s[s.length-1].split("[.]");
            package_class = s[0];

            final CompilationUnit cu = eJDTASTParser.parse(class_path) ;
            cu.accept(new ASTVisitor() {
				Set names = new HashSet();
				public boolean visit(PackageDeclaration node) {
					Name name = node.getName();
                    package_class = name+"."+package_class;
					return false; 
				}
			});
            if(!packages_classes.contains(package_class))
                packages_classes.add(package_class);
        }
        return packages_classes;
    }

}

