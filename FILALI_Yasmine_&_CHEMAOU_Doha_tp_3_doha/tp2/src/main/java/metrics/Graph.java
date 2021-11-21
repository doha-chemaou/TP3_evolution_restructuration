package metrics;

/** Auteurs : 
*	@author Yasmine FILALI 
*	@author Doha CHEMAOU
*/

import graphs.CallGraph;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Hashtable;

public class Graph {
    static Hashtable<String,Double> metrique = Metrique.metrique_with_lower_case;
    public static void dotGraph(CallGraph callGraph) throws IOException, ParseException{
        System.out.println("\u001B[93mFichier dot généré sous le nom \"dot_metrique.dot\"\u001B[0m");
        System.out.println("\n\u001B[36mFichier png généré sous le nom \"png_metrique.png\" pour visualiser le graphe\u001B[0m");

        Metrique.numOfCallsBetweenCallerAndCalle(callGraph);
        Metrique.metrique_couplage();
        try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dot_metrique.dot")))){
            out.write("digraph G{"); 
            out.newLine();
            for(String key: metrique.keySet()){

                String[] classe1AND2 = key.split("--->");
                String class1 = classe1AND2[0].trim(),class2=classe1AND2[1].trim() ;
                
                out.write(class1 + " -> " + class2 + " [ label = \""+metrique.get(key)+"\" ]");
                out.newLine();
            }
            out.write("}");
        }

        Process process = Runtime.getRuntime().exec("dot dot_metrique.dot -Tpng -o png_metrique.png",null);
    }
}

