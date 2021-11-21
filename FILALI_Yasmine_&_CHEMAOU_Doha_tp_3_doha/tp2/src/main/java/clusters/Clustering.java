package clusters;

/** Auteurs : 
*	@author Yasmine FILALI 
*	@author Doha CHEMAOU
*/

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import graphs.CallGraph;
import metrics.*;

public class Clustering {
    static LinkedHashMap<String,Double> metrique = new LinkedHashMap<>();
    static LinkedHashMap<String,Double> sorted_metrique = new LinkedHashMap<>();
    static LinkedHashMap<String,Integer> i_metrique = new LinkedHashMap<>();
    static List<Map.Entry<String, Integer> > list;
    static List liste = new ArrayList<>();
    static List<String> taken = new ArrayList<>();
    public static List respective_weights = new ArrayList<>();
    static List lolo = new ArrayList<>();
    public static String body = "";
    static List to_complete_clustering = new ArrayList<>();
    static List to_complete_clustering_nodes = new ArrayList<>();
    public static List<Object> together = new ArrayList<>();
    // public static BufferedWriter out;
    static int index_ = 1;
    public static LinkedHashMap<String,Double> sorted_metrique(CallGraph callGraph) throws IOException, ParseException{

        Metrique.numOfCallsBetweenCallerAndCalle(callGraph);
        Metrique.metrique_couplage();
        metrique=new LinkedHashMap<>(Metrique.getMetrique());
        // metrique.put("A ---> B",14.0) ;
        // metrique.put("C ---> D",13.0) ;
        // metrique.put("E ---> B",11.0) ;
        // metrique.put("C ---> F",10.0) ;
        // metrique.put("D ---> G",15.0) ;
        // metrique.put("H ---> B",9.0) ;

        // metrique.put("A ---> B",14.0) ;
        // metrique.put("C ---> D",13.0) ;
        // metrique.put("D ---> C",12.0) ;
        // metrique.put("E ---> B",11.0) ;
        // metrique.put("C ---> F",10.0) ;
        // metrique.put("D ---> G",15.0) ;
        // metrique.put("H ---> B",9.0) ;
        // metrique.put("D ---> F",8.0) ;
        // metrique.put("G ---> B",7.0) ;
        // metrique.put("B ---> A",6.0) ;
        // metrique.put("B ---> H",5.0) ;
        // metrique.put("I ---> B",4.0) ;
        
        double_to_int();
        list = new ArrayList<Map.Entry<String, Integer> >(i_metrique.entrySet());

        Collections.sort(
            list,
            new Comparator<Map.Entry<String, Integer> >() {
                public int compare(Map.Entry<String, Integer> entry1,Map.Entry<String, Integer> entry2){
                     return entry2.getValue() - entry1.getValue();
                }
            });

            int_to_double();
        return sorted_metrique;
    }

    static void double_to_int(){
        for (String key : metrique.keySet()){
            Double value=metrique.get(key);
            int i_value = (int)(value*10*10*10);
            i_metrique.put(key,i_value);
        }
    }

    static void int_to_double(){
        for (Map.Entry<String, Integer> l : list) {
            Double d_value = (double)l.getValue()/(double)(10*10*10);
            sorted_metrique.put(l.getKey(),d_value);
        }
    }

    public static void clustering_(CallGraph callGraph) throws ParseException, IOException{
        metrique = sorted_metrique(callGraph);
        taken = new ArrayList<>();

        together = new ArrayList<>();
        respective_weights = new ArrayList<>();
        lolo = new ArrayList<>();
        for(String key : metrique.keySet()){
            String[] classA_classB = key.split("--->");
            String class_a = classA_classB[0].trim();
            String class_b = classA_classB[1].trim();
            Double weight = sorted_metrique.get(key);

            if(!taken.contains(class_a) && !taken.contains(class_b)){
                List l = new ArrayList<>();
                l.add(class_a);
                l.add(class_b);
                together.add(l);
                l = new ArrayList<>();
                l.add(weight);
                respective_weights.add(l);
                taken.add(class_a);
                taken.add(class_b);
            }
        }
                
        for(String key_ : metrique.keySet()){
            String[] classA_classB = key_.split("--->");
            String class_a = classA_classB[0].trim();
            String class_b = classA_classB[1].trim();     

            contains__(together,class_a,class_b,0,together,0);
            
            if(!liste.isEmpty() && !together.contains(liste)){
                int i = 0 ; 
                while(i < together.size() && !liste.contains(together.get(i))){
                    i++;
                }
                if(i < together.size()){
                    together.set(i,liste);
                    List previsous_respective_weights = (List) respective_weights.get(i);
                    lolo.add(previsous_respective_weights);
                    respective_weights.set(i,lolo);
                }
            }
        }
        // to_dot(together);
    }
    public static void clustering(CallGraph callGraph) throws ParseException, IOException{
        clustering_(callGraph);
        to_dot(together);
    }

    public static void contains__(List<Object> l,String class_name_a,String class_name_b, int browser, List<Object> the_list,int browser_){
        
        if(browser == l.size()) return ;//new ArrayList<>();
        else{
            if(l.get(browser) instanceof List){
                if(!is_there(class_name_a,((List<Object>) l.get(browser)),0) && is_there(class_name_b,((List<Object>) l.get(browser)),0)){
                    if(!taken.contains(class_name_a)){
                        List<Object> l_ = new ArrayList<>();
                        l_.add(class_name_a);
                        if(l == the_list)
                            l_.add(the_list.get(browser));
                        else l_.add(the_list);
                        lolo = new ArrayList<>();
                        lolo.add(metrique.get(class_name_a+" ---> "+class_name_b));

                        taken.add(class_name_a);
                        liste = l_;
                        return ;//l_;
                }
            }
                else{
                        if(((List<Object>)l.get(browser)).get(1) instanceof List){
                            contains__((List<Object>)(((List<Object>)l.get(browser))),class_name_a,class_name_b,0,(List<Object>)l.get(browser),browser_+1);

                        }
                        if(browser+1 < l.size()){
                            contains__(l,class_name_a,class_name_b,browser+1,l,browser_);
                        }
                       
                }
                
            }  
            else{
                if(l instanceof List && !(l.get(browser) instanceof List)){
                    if(!is_there(class_name_a,the_list,0) && is_there(class_name_b,the_list,0)){
                        if(!taken.contains(class_name_a)){
                            List<Object> l_ = new ArrayList<>();
                            l_.add(class_name_a);
                            l_.add(the_list);
                            taken.add(class_name_a);
                            liste = l_;
                            lolo = new ArrayList<>();
                            lolo.add(metrique.get(class_name_a+" ---> "+class_name_b));
                            return ;//l_;
                    }
                }
                else{
                        if(browser+1<the_list.size() && the_list.get(browser+1) instanceof List){
                            contains__((List<Object>) l.get(browser+1),class_name_a,class_name_b,browser+1,l,browser_+1);
                        }
                }
                }
            
        }
                
            
        }
        
    }
   
    static public Boolean is_there(String element , List l, int browser){
        if(browser==l.size()){ 
            return false;}
        else{
            if(l.get(browser) instanceof List){
                if(l.get(browser) instanceof List && ((List<Object>)l.get(browser)).contains(element)) return true;
                else return is_there(element,(List<Object>)l,browser+1);

            }
            else{
                if(l.contains(element)) return true;
                else {
                    if(browser+1 < l.size() && l.get(browser+1) instanceof List) return is_there(element,(List<Object>)l.get(browser+1),0);
                    else return false;
                }
            } 
        }
    }

    static public void to_dot(List together) throws IOException{
        System.out.println("\033[35mFichier dot généré sous le nom \"dot_clustering.dot\"\u001B[0m");
        System.out.println("\n\033[95mFichier png généré sous le nom \"png_clustering.png\" pour visualiser le graphe\u001B[0m");
        // just to test the method , delete that to get the previous result 

        if(!together.isEmpty() && !respective_weights.isEmpty()){
            body = "";
            try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dot_clustering.dot")))){
                out.write("digraph G{\nedge [dir=none]"); 
                out.newLine();
                index_=1;
                to_complete_clustering = new ArrayList<>();
                to_complete_clustering_nodes = new ArrayList<>();
                dot_dot(together, respective_weights, 0 , 1,(List)together.get(0),(List)respective_weights.get(0),false);
                // System.out.println("complete clustering "+to_complete_clustering);
                // System.out.println("complete clustering nodes "+to_complete_clustering_nodes);

                finalize_dot();
                out.write(body); 
                out.write("}");

                Process process = Runtime.getRuntime().exec("dot dot_clustering.dot -Tpng -o png_clustring.png",null);
                out.close();
                }

            }
        }
        static public void dot_dot(List together ,List respect_weights, int browser, int index,List current_list,List current_resp, Boolean passage) throws IOException{
            if(browser == together.size()) return ; 
            else{
                // ce cas : [ A , B ]
                if(!(current_list.get(1) instanceof List)){
                    body+="node"+index+" [style = filled, color = mediumorchid2, label="+((List<Object>)current_list).get(0)+", shape = rectangle]\n";
                    body+="node"+(index+1)+" [style = filled, color = mediumorchid2, label="+((List<Object>)current_list).get(1)+", shape = rectangle]\n";
                    body+="node"+(index+2)+" [color = mediumorchid2, label=\""+((List<Object>)current_resp).get(0)+"\", shape = rectangle]\n";
                    body+="node"+(index+2)+" -> node"+index+"\n";
                    body+="node"+(index+2)+" -> node"+(index+1)+"\n";
                    if(!passage){
                        to_complete_clustering.add(((List<Object>)current_resp).get(0));
                        // System.out.println("node"+(index+2));
                        to_complete_clustering_nodes.add("node"+(index+2));
                    } 
                    if(browser+1 < together.size())
                        dot_dot(together, respect_weights, browser+1,index+3,(List)together.get(browser+1),(List)respect_weights.get(browser+1),false);
                    else 
                        dot_dot(together, respect_weights, browser+1,index+3,current_list,current_resp,false);
                    
                    index_+=3;
                }
                else{
                    body+="node"+index+" [style = filled, color = mediumorchid2, label="+((List<Object>)current_list).get(0)+", shape = rectangle]\n";
                    body+="node"+(index+1)+" [color = mediumorchid2, label=\""+((List<Object>)current_resp).get(0)+"\", shape = rectangle]\n";

                    body+="node"+(index+1)+" -> node"+index+"\n";
                    if(body != ""){
                        if(((List)current_list.get(1)).get(1) instanceof List)
                            body+="node"+(index+1)+" -> node"+(index+3)+"\n";
                        else
                            body+="node"+(index+1)+" -> node"+(index+4)+"\n";
                        if(!passage){
                            to_complete_clustering.add(((List<Object>)current_resp).get(0));
                            to_complete_clustering_nodes.add("node"+(index+1));
                        } 
                    }
                    index_+=2;
                    dot_dot(together, respect_weights, browser, index+2, (List)current_list.get(1),(List)current_resp.get(1),true);
                }
            }
        }

        static public void finalize_dot(){
        while(to_complete_clustering.size() > 1 && to_complete_clustering_nodes.size() > 1){

            Double d1=Double.parseDouble(to_complete_clustering.get(0).toString()),d2=Double.parseDouble(to_complete_clustering.get(1).toString());
            to_complete_clustering.set(0,d1+d2);
            if(!(to_complete_clustering_nodes.get(0) instanceof List))
                to_complete_clustering.remove(1);

            Double d = d1+d2;
            
            body+="\nnode"+(index_)+" [color = mediumorchid2, label=\""+d+"\", shape = rectangle]\n";
            if(!(to_complete_clustering_nodes.get(0) instanceof List)){

                body+="node"+(index_)+" -> "+to_complete_clustering_nodes.get(0)+"\n";
                body+="node"+(index_)+" -> "+to_complete_clustering_nodes.get(1)+"\n";
            }
            else{

                body+="node"+(index_)+" -> "+to_complete_clustering_nodes.get(1)+"\n";
                body+="node"+(index_)+" -> "+"node"+(index_-1)+"\n";

            }
            index_++;
            if((to_complete_clustering_nodes.get(0) instanceof List)){
                to_complete_clustering.remove(1);
            }

            List l = new ArrayList<>();
            l.add(to_complete_clustering_nodes.get(0));
            l.add(to_complete_clustering_nodes.get(1));

            to_complete_clustering_nodes.set(0,l);
            to_complete_clustering_nodes.remove(1);
         }
        }
}
