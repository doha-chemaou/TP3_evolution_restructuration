package metrics;
import graphs.CallGraph;
/**
 * @author FILALI Yasmine
 * @author CHEMAOU Doha
 */
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Hashtable;

public class Graph {
    static Hashtable<String,Double> metrique = Metrique.metrique_with_lower_case;
    public static void dotGraph(CallGraph callGraph) throws IOException, ParseException{
        System.out.println("\u001B[93mFichier dot généré sous le nom \"metrique.dot\"\u001B[0m");

        Metrique.numOfCallsBetweenCallerAndCalle(callGraph);
        Metrique.metrique_couplage();
        String graph = "graph%7B";
            try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("metrique.dot")))){
                out.write("digraph G{"); 
                out.newLine();
                for(String key: metrique.keySet()){
                    String[] classe1AND2 = key.split("--->");
                    String class1 = classe1AND2[0].trim(),class2=classe1AND2[1].trim() ;
                    // [ label = "0.091 "]
                    out.write(class1 + " -> " + class2 + " [ label = \""+metrique.get(key)+"\" ]");
                    graph += class1 + "%2D%2D" + class2+"%5Blabel%3D%22"+metrique.get(key)+"%22%5D%3B"+"%20";// + "%5Blabel%3D%22"+metrique.get(key)+"%22%5D"+"%20";
                  out.newLine();
                }
                String url_open ="https://quickchart.io/graphviz?graph="+graph+"%7D";
                out.write("}");
                if(url_open.length()<2500)
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
              }
        }
    }

