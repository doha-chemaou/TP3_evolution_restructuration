package partition;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import clusters.*;
import graphs.CallGraph;

public class Partition {
    
    static LinkedHashMap<String,Double> metrique = new LinkedHashMap<>();
    static LinkedHashMap<String,Double> sorted_metrique = new LinkedHashMap<>();
    static LinkedHashMap<String,Integer> i_metrique = new LinkedHashMap<>();
    static List<Map.Entry<String, Integer> > list;
    static List liste = new ArrayList<>();
    static List<String> taken = new ArrayList<>();
    static List lolo = new ArrayList<>();
    static String body = "";
    static int index = 1;
    static int index_ = 1;
    static int index__ = 0;

    public static void partition(CallGraph callGraph) throws ParseException, IOException{
        body = "";

        Clustering.clustering_(callGraph);

        to_dot(Clustering.together);
   
    }

    static public void dot_dot(List together ,List respect_weights, int browser, int index,List current_list,List current_resp) throws IOException{
        if(browser == together.size()) return ; 
        else{
            // ce cas : [ A , B ]
            if(!(current_list.get(1) instanceof List)){
                body+="node"+index+" [style = filled, color = chartreuse, label="+((List<Object>)current_list).get(0)+", shape = rectangle]\n";
                body+="node"+(index+1)+" [style = filled, color = chartreuse, label="+((List<Object>)current_list).get(1)+", shape = rectangle]\n";
                body+="node"+(index+2)+" [color = chartreuse, label=\""+((List<Object>)current_resp).get(0)+"\", shape = rectangle]\n";
                body+="node"+(index+2)+" -> node"+index+"\n";
                body+="node"+(index+2)+" -> node"+(index+1)+"\n";

                if(browser+1 < together.size()){
                    index__++;
                    body+="}\nsubgraph cluster_"+ index__+"{\n";
                    dot_dot(together, respect_weights, browser+1,index+3,(List)together.get(browser+1),(List)respect_weights.get(browser+1));

                }
                else {
                    body+="}";
                    dot_dot(together, respect_weights, browser+1,index+3,current_list,current_resp);
                }
                
                index_+=3;
            }
            else{
                body+="node"+index+" [style = filled, color = chartreuse, label="+((List<Object>)current_list).get(0)+", shape = rectangle]\n";
                body+="node"+(index+1)+" [color = chartreuse, label=\""+((List<Object>)current_resp).get(0)+"\", shape = rectangle]\n";

                body+="node"+(index+1)+" -> node"+index+"\n";
                if(body != ""){
                    if(((List)current_list.get(1)).get(1) instanceof List)
                        body+="node"+(index+1)+" -> node"+(index+3)+"\n";
                    else
                        body+="node"+(index+1)+" -> node"+(index+4)+"\n";
               
                }
                index_+=2;
                dot_dot(together, respect_weights, browser, index+2, (List)current_list.get(1),(List)current_resp.get(1));
            }
        }
    }

    static public void to_dot(List together) throws IOException{
        System.out.println("\033[92mFichier dot généré sous le nom \"dot_partition.dot\"\u001B[0m");
        System.out.println("\n\033[32mFichier png généré sous le nom \"png_partition.png\" pour visualiser le graphe\u001B[0m");
        if(!together.isEmpty() && !Clustering.respective_weights.isEmpty()){
            body = "";
            try(BufferedWriter out=new BufferedWriter(new FileWriter("dot_partition.dot"))){
                out.write("digraph G{\nedge [dir=none]\ngraph [fontsize=10 fontname=\"Verdana\"]\nnode [shape=record fontsize=10 fontname=\"Verdana\"]\n\nsubgraph cluster_"+index__+"{"); 
                out.newLine();
                index_=1;
                dot_dot(together, Clustering.respective_weights, 0 , 1,(List)together.get(0),(List)Clustering.respective_weights.get(0));
            
                out.write(body); 
                out.write("\n}\n}");

                Process process = Runtime.getRuntime().exec("dot dot_partition.dot -Tpng -o png_partition.png",null);
                out.close();

            }
            catch (FileNotFoundException ex){
                System.out.println(ex);
           }
           catch (IOException ex){
               System.out.println(ex);
           }
        }
    }

}
