package metrics;

/**
 * @author FILALI Yasmine
 * @author CHEMAOU Doha
 */

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.NumberFormat;

import org.eclipse.jdt.internal.core.search.matching.MatchLocatorParser.ClassAndMethodDeclarationVisitor;

import graphs.CallGraph;
import packagesANDclasses.Package_Class;

public class Metrique {
    // has just those who appear to be calling one another
    static Hashtable <String,Integer> classA_classB_numberOfCalls = new Hashtable<>();
    static Hashtable <String,Double> metrique = new Hashtable<>();
    static Hashtable <String,Double> metrique_with_lower_case = new Hashtable<>();

    static List classes;// = Package_Class.classes;  
    static List classes_with_lower_cases ; 
    // has all the classes event hough they don't one another
    // static Hashtable <String,Integer> classA_classB_numberOfCalls_ = new Hashtable<>();

    public static void numOfCallsBetweenCallerAndCalle(CallGraph graph) throws IOException{
        List packages_classes = Package_Class.list_of_package_class();
        Map<String, Map<String, Integer>> invocations = graph.getInvocations();

        // System.out.println("***********************************");

		for (String source : invocations.keySet()) {
			// String[] s = source.split("::");
            String source_package_class = getPackage_Class(source);
            if(packages_classes.contains(source_package_class)){
                for (String destination : invocations.get(source).keySet()){
                    // System.out.println(invocations.get(source).get(destination)); // number of calls
                    // System.out.println("_______________________");
                    // System.out.println(destination);
                    // System.out.println();
                    // s = destination.split("::");
                    String destination_package_class = getPackage_Class(destination);
                    if(!source_package_class.equals(destination_package_class) && packages_classes.contains(destination_package_class)){
                        String source_destination = source_package_class+"_____"+destination_package_class;
                        int number_of_calls = invocations.get(source).get(destination);
                        if(classA_classB_numberOfCalls.get(source_destination)!=null){
                            int previous_number_of_calls = classA_classB_numberOfCalls.get(source_destination);
                            number_of_calls += previous_number_of_calls;
                        }
                        classA_classB_numberOfCalls.put(source_destination,number_of_calls);
                    }
                }
            }	
		}

        // affichage
        /*for(String classA_classB : classA_classB_numberOfCalls.keySet()){
            System.out.println(classA_classB + " : " + classA_classB_numberOfCalls.get(classA_classB));
        }
        System.out.println(number_of_calls());*/
    }

    static String getPackage_Class(String sourceORdestination){
        String[] s = sourceORdestination.split("::");
        return s[0];
    }



    static int number_of_calls(){
        int number_of_calls = 0 ; 
        for (String classA_classB : classA_classB_numberOfCalls.keySet()){
            number_of_calls+= classA_classB_numberOfCalls.get(classA_classB);
        }
        return number_of_calls;
    }

    public static  String getClassA(String sourceANDdestionation){
        String class_ = "";
        String[] s = sourceANDdestionation.split("_____");
        String source = s[0];
        if(source.contains(".")){
            s = source.split("[.]");
            source = s[s.length-1];
        }
        class_ = source ; 
        return class_;
    }

    public static  String getClassB(String sourceANDdestionation){
        String class_ = "";
        String[] s = sourceANDdestionation.split("_____");
        String source = s[1];
        if(source.contains(".")){
            s = source.split("[.]");
            source = s[s.length-1];
        }
        class_ = source ; 
        return class_;
    }

    public static void metrique_couplage() throws ParseException{
        for (String classA_classB : classA_classB_numberOfCalls.keySet()){
            int number_of_calls = number_of_calls();
            // System.out.println(classA_classB_numberOfCalls.get(classA_classB));
            double metrique_f= (float) 0.0;
            if(number_of_calls!=0){
                metrique_f = classA_classB_numberOfCalls.get(classA_classB)/(float)number_of_calls;
                // Math.round(number*100)/100
                // metrique_f = Math.round(metrique_f*1000)/1000;//.toFixed(3);
 
                String metrique_s = new DecimalFormat("#.###").format(metrique_f);
                NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                Number number = format.parse(metrique_s);
                metrique_f = number.doubleValue();
            }
            String calling_callee = getClassA(classA_classB)+ " ---> " + getClassB(classA_classB);
            metrique.put(calling_callee,metrique_f);
            
            String calling_callee_lower = getClassA(classA_classB).toLowerCase()+" ---> " + getClassB(classA_classB).toLowerCase();
            metrique_with_lower_case.put(calling_callee_lower,metrique_f);

            // System.out.println("*************************");
            // System.out.println(calling_callee_lower + " "+metrique_f);
            // System.out.println(metrique.get(classA_classB));
        }
    }


    public static void affiche_metrique_couplage(CallGraph callGraph) throws IOException, ParseException{
        numOfCallsBetweenCallerAndCalle(callGraph);
        System.out.println("Le reste des couples des classes (qui n'apparaissent pas) ont naturellement une métrique égale à 0.0\n");
        metrique_couplage();
        // affichage
        for(String classA_classB : metrique.keySet()){
            System.out.println(classA_classB + " : " + metrique.get(classA_classB));
        }
        System.out.println();
        // System.out.println(number_of_calls());
    }

    public static void metrique_between_two_specific_classes(Scanner sc,CallGraph callGraph) throws IOException,ParseException{
        numOfCallsBetweenCallerAndCalle(callGraph);
        metrique_couplage();
        /*for(String classA_classB : metrique_with_lower_case.keySet()){
            System.out.println(classA_classB + " : " + metrique_with_lower_case.get(classA_classB));
        }*/
        System.out.println("\u001B[32mVoici la liste des classes qu'il y a sur l'ensemble de l'application Java :\u001B[0m");
        classes_with_lower_cases = Package_Class.classes_with_lower_cases;
        System.out.println(classes_with_lower_cases);
        Package_Class.list_of_package_class();
        classes = Package_Class.classes;
        
        // int count = 0 ;
        /*for (int i = 0 ; i < classes.size(); i++){
            // if(count == 4)
                // System.out.println(classes);
            String class_ = (String) classes.get(i);
            // System.out.println("hehehehehehh " +class_);
            System.out.print(class_.substring(0,class_.length()-5)+ "\t ");
            // count++;
        }*/
        System.out.print("\n\n\u001B[34mVeuillez entrer le nom de la première classe :\u001B[0m ");
        String classeA = sc.nextLine(); 
        classeA = classeA.trim(); 
        classeA = classeA.toLowerCase();
        // classeA = classeA.substring(0, 1).toUpperCase() + classeA.substring(1);
        System.out.print("\u001B[34mVeuillez entrer le nom de la deuxième classe :\u001B[0m ");
        String classeB = sc.nextLine(); 
        classeB = classeB.trim();
        classeB = classeB.toLowerCase();
        // classeB = classeB.substring(0, 1).toUpperCase() + classeB.substring(1);

        // System.out.println(classeA + " ---> "+ classeB);
        String classA_classB = classeA + " ---> " + classeB ;
        /*for(String key : metrique.keySet()){
            System.out.println(key+" "+metrique.get(key));
        }*/
        if(metrique_with_lower_case.get(classA_classB)!=null){
            // System.out.println(metrique_with_lower_case.keySet());
            System.out.println("la métrique de couplage est "+ metrique_with_lower_case.get(classA_classB));

        }
        else{
            // System.out.println(classes);
            // System.out.println(classeA+" "+classeB);
            // System.out.println(classes_with_lower_cases);

            if(classes_with_lower_cases.contains(classeA.toLowerCase()) && classes_with_lower_cases.contains(classeB.toLowerCase())){
                if(classeA.equals(classeB))
                    System.out.println("\u001B[31m Il n'est pas nécessaire de calculer la métrique entre une même classe \u001B[0m");

                else{
                    System.out.println("la métrique de couplage est 0.0");
                }
            }
            else{
                System.out.println("\u001B[31m!!! Veuillez vérifier l'orthographe, les classes demandées n'appartiennent pas à l'application Java\u001B[0m");
            }
        }
        
    }
}
