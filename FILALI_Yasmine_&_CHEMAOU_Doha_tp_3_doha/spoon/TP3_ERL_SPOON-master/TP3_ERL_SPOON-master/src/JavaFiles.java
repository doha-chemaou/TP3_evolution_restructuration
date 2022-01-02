import java.util.List;
import java.io.File;
import java.util.ArrayList;

public class JavaFiles {
	static List<String> files = new ArrayList<>();

	public static void javaFiles(String path) {
		files = new ArrayList<>();
        //List directories = new ArrayList<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
         
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
            	String file = listOfFiles[i].getName();
            	if(file.substring(file.length()-5,file.length()).equals(".java"))
            		files.add(path+listOfFiles[i].getName());
            } 
            else {
            	if (listOfFiles[i].isDirectory()) {
            		javaFiles(path+listOfFiles[i].getName()+"/");
            	}
            }
        }
	}
	
	public static List getjavaFiles() {
		return files;
	}
}
