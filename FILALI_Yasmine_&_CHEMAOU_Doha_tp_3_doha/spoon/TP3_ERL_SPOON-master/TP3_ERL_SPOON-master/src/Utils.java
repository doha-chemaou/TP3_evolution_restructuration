import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Utils {
	public static void generateFile(String fileName,String content){
		try {

			File f = getResource(fileName);
			if (!f.exists()){
				f.createNewFile();
			}
			Files.writeString(f.toPath(), content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static File getResource(String fileName) {
		final String completeFileName = getResourcePath(fileName);
		File file = new File(completeFileName);
		return file;
	}
	
	public static String getResourcePath(String fileName) {
		final File f = new File("");
		final String dossierPath = f.getAbsolutePath() + File.separator + fileName;
		return dossierPath;
	}
}
