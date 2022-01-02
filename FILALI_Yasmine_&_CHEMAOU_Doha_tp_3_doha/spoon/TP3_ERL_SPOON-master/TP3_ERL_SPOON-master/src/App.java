import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Hello world!
 *
 */
public class App {
	static String file_content ="";
	static List<String> classes_java_files = new ArrayList<>();
	static List<String> java_files = JavaFiles.getjavaFiles();
	static List<String> classes = new ArrayList<>();

	
	
	public static String read_file_at_once(String path) {
		File crunchifyFile = new File(path);
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(crunchifyFile);
			byte[] crunchifyValue = new byte[(int) crunchifyFile.length()];
			fileInputStream.read(crunchifyValue);
			fileInputStream.close();
 
			file_content = new String(crunchifyValue, "UTF-8");
			//log(fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file_content;
	}
	
	public static void class_java_files() {
		for(int i = 0 ; i < java_files.size();i++) {
			
		}
	}
    
	public static void app(){
		Launcher launcher = new Launcher();//"C:/mes_dossiers/S3/913 - evolution et restructuration des modeles/TP3/tp_comprension_logiciel_hai913i-main/tp_2_doha/tp2/App java/src/", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
		launcher.addInputResource("C:/mes_dossiers/S3/913 - evolution et restructuration des modeles/TP3/tp_comprension_logiciel_hai913i-main/tp_2_doha/tp2/App java/src/"); 

		launcher.buildModel();
		//launcher.buildModel();
		CtModel model = launcher.getModel();

		// list all packages of the model
		/*for(CtPackage p : model.getAllPackages()) {
		  System.out.println("package: " + p.getQualifiedName());
		}
		// list all classes of the model
		for(CtType<?> s : model.getAllTypes()) {
		  System.out.println("class: " + s.getQualifiedName());
		}*/
//		System.out.println("--------------------------");
	    for (CtClass<?> klass : model.getElements(new TypeFilter<CtClass>(CtClass.class))) {
			  //System.out.println("class: " + klass.getQualifiedName());
			if(classes.contains(klass.getQualifiedName()))  
				classes.add(klass.getQualifiedName());
	    }


    	/*List<String> java_files = JavaFiles.getFiles();
    	//System.out.println(java_files.size());
    	//System.out.println("    "+java_files.get(8));
    	
    	for (int i = 0 ; i < java_files.size();i++) {
    		String path = java_files.get(i);
    		System.out.println("i "+i+" "+path);

    		read_file_at_once(path);
    		
        	CtClass l = Launcher.parseClass(file_content);//Launcher.parseClass("class A { void m() { System.out.println(\"yeah\");} }");
        	System.out.println("heyheyhey\n"+l);
        	//Launcher.parseClass((String)java_files.get(i));
    	}*/
    	
    }
}
