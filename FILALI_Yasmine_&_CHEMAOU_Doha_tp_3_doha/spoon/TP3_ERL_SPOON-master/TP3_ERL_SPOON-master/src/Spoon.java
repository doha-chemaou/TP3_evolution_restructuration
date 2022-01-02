import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class Spoon {
	static final DecimalFormat df = new DecimalFormat("0.000");
	static HashMap<String, HashMap<String, Integer>> statsPerClass = new HashMap<String, HashMap<String, Integer>>();
	private static ArrayList<File> javaFiles;
	private static HashMap<String, Integer> methodCallByClass = new HashMap<String, Integer>();
	private static List<String> classNames = new ArrayList();
	private static List<CtType<?>> parsedClasses = new ArrayList();
	static HashMap<List<String>, Integer> combinaisonStats = new HashMap<List<String>, Integer>();
	static String graph = "graph G{ \n";
	static int numberOfAllClassCombinaisonCall = 0;
	static List<String> allCombinaisonTwoClassNames = new ArrayList<String>();
	private static int number;
	private static int[] pairClasses = new int[2];
	static StringBuilder mergeLogger = new StringBuilder("");
	static int metricValue = 0;
	static Stack<List> pile = new Stack<List>();
	static HashMap<List<String>, Node> nodeElement = new HashMap<List<String>, Node>();
	static Node root = null;
	static boolean showGraph = true;
	static String tree = "";
	public static final String QUIT = "0";
	public static String TEST_PROJECT_PATH;
	static String graph1 = "graph G{ \n";
	static int counter = 0;
	static int metrique = 0; 
	static String last_line_of_graph = "";
	static String graph2 = "digraph G{\n"
			+ "edge [dir=none]\n"
			+ "graph [fontsize=10 fontname=\"Verdana\"]\n"
			+ "node [shape=record fontsize=10 fontname=\"Verdana\"]";

	private static ArrayList<File> getFiles(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(getFiles(fileEntry));
			} else if (fileEntry.getName().contains(".java")) {
				javaFiles.add(fileEntry);
			}
		}
		return javaFiles;
	}
	static protected void processUserInput(String userInput){
		Scanner sc = new Scanner(System.in);
		try {
			
			switch (userInput) {
			case "1": {
				Spoon.parseProject(new File(TEST_PROJECT_PATH).getAbsolutePath());
				Spoon.generateGraph("spoonCoupling");

				break;
			}
			case "2":{
				Spoon.parseProject(new File(TEST_PROJECT_PATH).getAbsolutePath());
				Spoon.generateGraphWithoutShowing();
				Spoon.question1();
				break;
			}
			case "3":{
				Spoon.parseProject(new File(TEST_PROJECT_PATH).getAbsolutePath());
				Spoon.generateGraphWithoutShowing();
				Spoon.question1withoutshowing();
				Spoon.question2();
//				System.out.println(graph2);
			}
			
			case QUIT:
				System.out.println("Bye...");
				return;

			default:
				System.err.println("Sorry, wrong input. Please try again.");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	static protected void menu() {
		StringBuilder builder = new StringBuilder();
		builder.append("\t\t -----------------------------------------Menu--------------------------------------------\n");
		builder.append("\t\t|1. Métrique de couplage entre chaque deux classes (using spoon)                          |");
		builder.append("\n\t\t|2. regroupement hiérarchique (clustering) (using spoon)                                  |");
		builder.append("\n\t\t|3. identification de groupe de classes couplées (using spoon)                            |");
		builder.append("\n\t\t|" + QUIT + ". To quit                                                                               |");
		builder.append("\n\t\t -----------------------------------------------------------------------------------------");

		System.out.println(builder);
	}
	
	protected static void setTestProjectPath(BufferedReader inputReader) throws IOException {
		File projectFolder;

		System.out.println("\n\nPlease provide the path to a java project's src/ folder: ");
		TEST_PROJECT_PATH = inputReader.readLine();
		projectFolder = new File(TEST_PROJECT_PATH);

		while (!projectFolder.exists() || !TEST_PROJECT_PATH.endsWith("src/")) {
			System.err.println("\u001B[31mError: " + TEST_PROJECT_PATH
					+ " either doesn't exist or isn't a java project src/ folder. " + "Please try again: \u001B[0m");
			TEST_PROJECT_PATH = inputReader.readLine();
			projectFolder = new File(TEST_PROJECT_PATH);
		}
	}

	public static void parseProject(String path) throws IOException {
		javaFiles = getFiles(new File(path));
		parsedClasses=new ArrayList<>();
		classNames = new ArrayList<>();


		for (File file : javaFiles) {
			

			try {
				
				CtType<?> classe = Launcher.parseClass(FileUtils.readFileToString(file));
				String[] classes = file.toString().split("\\\\");
				int index = classes.length-1;

				classNames.add(classe.getSimpleName());
				parsedClasses.add(classe);
				statsPerClass.put(classe.getSimpleName(), new HashMap<String, Integer>());
			}catch(Exception e) {
			}
			
		}
			
		}

	private static String getClassName(String className) {
		for (String classN : classNames) {
			if (classN.equals(className))
				return classN;
		}
		return "";
	} 
	
	private static void wrapMethods(CtType<?> classe) {
		String className = classe.getSimpleName();
		HashMap<String, Integer> methodCallByClass = new HashMap<String, Integer>();
		if (statsPerClass.get(className) != null) {
			methodCallByClass = statsPerClass.get(className);
		}
		for(CtMethod<?> method : classe.getAllMethods()) {
			for (CtInvocation<?> methodInvocation : (List<CtInvocation>) Query.getElements(method,
					new TypeFilter<CtInvocation>(CtInvocation.class)) ) {
				if (methodInvocation != null && methodInvocation.getTarget() != null
						&& methodInvocation.getTarget().getType() != null) {
					if (!classe.getQualifiedName().equals(methodInvocation.getTarget().getType().toString())) {
						String classNameOfMethod = getClassName(methodInvocation.getTarget().getType().toString());
						if(!classNameOfMethod.equals("")) {
							
							if (methodCallByClass.get(classNameOfMethod) != null && classNames.contains(classNameOfMethod)) {
								int numberCalls = methodCallByClass.get(classNameOfMethod);
								methodCallByClass.put(classNameOfMethod, ++numberCalls);

							} else if(methodCallByClass.get(classNameOfMethod) == null ) {
								// vide
								methodCallByClass.put(classNameOfMethod, 1);
							}
						}
					
					}
				}
			}
		}

	}
	public static void generateGraphWithoutShowing() throws IOException {
		graph = "graph G{ \n";
		for(CtType<?> parsedClass : parsedClasses) {
			wrapMethods(parsedClass);
		}
		classNames.forEach(className -> {
			combinaisonStats .put(List.of(className), 0);
			

		});
		allCombinaisonTwoClassNames = generateAllCombinaisonOfClassNames(classNames);
		calculateAllCalls();
		allCombinaisonTwoClassNames.forEach(combinaison -> {
			String[] splitedCombinaison = combinaison.split(";");
			String className1 = splitedCombinaison[0];
			String className2 = splitedCombinaison[1];
			int[] calls = getNumberCallsOfTwoClasses(className1, className2);
			
			
			double metrics = numberOfAllClassCombinaisonCall != 0
					? ((double) calls[0]+calls[1] / numberOfAllClassCombinaisonCall)
					: 0.;
			String m = df.format(metrics) + " (" + (calls[0]+calls[1]) + "/" + numberOfAllClassCombinaisonCall + ")";

			if(calls[0]+calls[1] != 0)
				graph += className1 + "--" + className2 + " [ label=\"" + m + "\" ] \n";


		});
		graph += "}";
	}


	public static void generateGraph(String nameofgraph) throws IOException {
		for(CtType<?> parsedClass : parsedClasses) {
			wrapMethods(parsedClass);
		}
		System.out.println("________________________________________________________\n");
		classNames.forEach(className -> {
			System.out.println("\nclass " + className + ": ");
			combinaisonStats .put(List.of(className), 0);
			if (statsPerClass.get(className).isEmpty())
				System.out.println("no calls");
			else
				statsPerClass.get(className).forEach((key, value) -> {
					
					System.out.println("There are "+value+" calls of methods from class "+key);

				});

		});
		System.out.println("\n________________________________________________________\n");
		allCombinaisonTwoClassNames = generateAllCombinaisonOfClassNames(classNames);
		calculateAllCalls();
		graph = "graph G{ \n";
		allCombinaisonTwoClassNames.forEach(combinaison -> {
			String[] splitedCombinaison = combinaison.split(";");
			String className1 = splitedCombinaison[0];
			String className2 = splitedCombinaison[1];
			int[] calls = getNumberCallsOfTwoClasses(className1, className2);
			
			
			double metrics = numberOfAllClassCombinaisonCall != 0
					? ((double) calls[0]+calls[1] / numberOfAllClassCombinaisonCall)
					: 0.;
			String m = df.format(metrics) + " (" + (calls[0]+calls[1]) + "/" + numberOfAllClassCombinaisonCall + ")";

			if(calls[0]+calls[1] != 0)
				graph += className1 + "--" + className2 + " [ label=\"" + m + "\" ] \n";

		});
		graph += "}";
		// System.out.println(graph);
		if (graph.toCharArray().length < 2500) {
			show(graph);
		}

		else {
			showGraph=false;
			System.out.println("the graph is a little bit large, so we're not showing it");
		}
		System.out.println("\n--------------------> file "+ nameofgraph+".dot is generated in fodler 'results\n");
		String fileName = nameofgraph+".dot";
		Utils.generateFile(fileName, graph.toString());
		
		
		
	}
	private static void show(String graph_) throws IOException {
		String url = "";
		try {
			url = "https://quickchart.io/graphviz?graph=" + URLEncoder.encode(graph_.toString(), "UTF-8");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {

				desktop.browse(URI.create(url));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private static int[] getNumberCallsOfTwoClasses(String className1, String className2) {
		number = 0;
		pairClasses  = new int[2];
		classNames.forEach(className -> {
			if (className.equals(className1) || className.equals(className2)) {
				statsPerClass.get(className).forEach((key, value) -> {

						if(!key.equals(className)) {
							if (key.equals(className1)) {
								pairClasses[0] = value;
							} else if (key.equals(className2)) {
								pairClasses[1] = value;

							}	
						}
						
					

				});
			}

		});

		return pairClasses;
	}
	private static void calculateAllCalls() {
		numberOfAllClassCombinaisonCall = 0;
		allCombinaisonTwoClassNames.forEach(combinaison -> {
			String[] splitedCombinaison = combinaison.split(";");
			String className1 = splitedCombinaison[0];
			String className2 = splitedCombinaison[1];
			numberOfAllClassCombinaisonCall += getNumberCallsOfTwoClasses(className1, className2)[0]
					+ getNumberCallsOfTwoClasses(className1, className2)[1];
			
		});
		
	}

	private static List<String> generateAllCombinaisonOfClassNames(List<String> classNames2) {
		List<String> allCombinaisonTwoClassName = new ArrayList<String>();
		if (classNames.size() >= 2)
			for (int i = 0; i < classNames.size(); i++) {
				for (int j = i + 1; j < classNames.size(); j++) {
					String combinaison = classNames.get(i) + ";" + classNames.get(j);
					allCombinaisonTwoClassName.add(combinaison);
				}
			}
		return allCombinaisonTwoClassName;
	}



	public static void question2() throws IOException {
		graph2 =  "digraph G{\n"
				+ "edge [dir=none]\n"
				+ "graph [fontsize=10 fontname=\"Verdana\"]\n"
				+ "node [shape=record fontsize=10 fontname=\"Verdana\"]";
		List<List<String>> partitions = selectionCluster(root);
		
		System.out.println("Partions :");
		counter = 0;
		int i =0;
		for(List<String> partition : partitions) {
			++i;
			System.out.println("Partition "+i+" : "+partition.toString());
			graph2 +="\nsubgraph cluster_"+(i-1)+"{\n";
			
			if(partition.size()==1) {
				graph2+="node"+counter+"[style = filled, color = chartreuse, label="+partition.get(0)+", shape = rectangle]\n}";
				graph2+="node"+counter;
				counter++;
			}
			else {
				for(String part : partition) {
					graph2+="node"+counter+"[style = filled, color = chartreuse, label="+part+", shape = rectangle]\n";
					if(counter >=1) {
						graph2+="node"+(counter+1)+"[color = chartreuse, label=\"\", shape = point]\n";
						graph2+="node" + counter +" -> "+"node"+(counter+1)+"\n";
						graph2+="node"+(counter-1)+" -> "+"node"+(counter+1)+"\n";
						counter+=2;
					}
					else {
						counter++;

					}
				}
				graph2+="}\n";
			}
		}
		graph2+="}";
		System.out.println("\n--------------------> file spoonPartition.dot is generated\n");
		String fileName = "spoonPartition.dot";
		Utils.generateFile(fileName, graph2.toString());
		show(graph2);
	}
	

	private static List<List<String>> selectionCluster(Node<List> dendro) {
		List<List<String>> partition = new ArrayList<>();

		pile.push(dendro.getData());
		do {
			List pere = pile.pop();
			Node nodePere = dendro.find(pere);
			List<Node> childreen = null;
			if(nodePere.getChildren().size()!=0) {
				
		
			childreen = nodePere.getChildren();
			
			List<String> fils1 = new ArrayList<String>();
			if (childreen.get(0).getData() instanceof List) {
				fils1 = (List<String>) childreen.get(0).getData();
			} else {
				fils1 = List.of(childreen.get(0).getData().toString());
			}
			List<String> fils2 = new ArrayList<String>();
			if (childreen.get(1).getData() instanceof List) {
				fils2 = (List<String>) childreen.get(1).getData();
			} else {
				fils2 = List.of(childreen.get(1).getData().toString());
			}

		
			if (S(nodePere) > (S(childreen.get(0))+S(childreen.get(0)))/2) {
				partition.add(pere);
			} else {
				pile.push(fils1);
				pile.push(fils2);
				
			}
			}else {
				partition.add((List<String>) nodePere.getData());
			}

		} while (!pile.isEmpty());

		return partition;
	}


	
	private static int S(Node nodePere) {
		// TODO Auto-generated method stub
		return nodePere.getNodeMetric();
	}


	public static int Somme(List<String> partition) {
		int somme = 0;
		for (int i = 0; i < partition.size(); i++) {
			for (int j = i + 1; j < partition.size(); j++) {
				String className1 = partition.get(i);
				String className2 = partition.get(j);
				int stats[] = getNumberCallsOfTwoClasses(className1, className2);
				somme += stats[0] + stats[1];

			}
		}
		return somme;
	}

	public static void question1() throws IOException{
		Set<String> classes = new LinkedHashSet<String>(classNames);
		mergeLogger.append(combinaisonStats + "\n");
		// Setup dendro Nodes
		combinaisonStats.keySet().forEach(element -> {
			nodeElement.put(element, new Node<List<String>>(List.of(element.get(0)), 0));
		});

		do {

			mergeWithMaxCombinaisonCall();
			
		} while (combinaisonStats.size()!=1);
		System.out.println(combinaisonStats);
		combinaisonStats.forEach((key, value) -> {
			root = nodeElement.get(key);
		});
		
		if(showGraph) {
			System.out.println("dendro: ");
			treeToString(root);
			treatinggraph();
			if(!graph1.contains("}"))
				graph1 += "}";
			System.out.println(tree);
			
//			System.out.println(graph1);
			if (graph1.toCharArray().length < 2500) {
				show(graph1);
			}
			else {
				showGraph=false;
				System.out.println("the graph is a little bit large, so we're not showing it");
			}
			
			System.out.println("\n--------------------> file spoonClustering.dot is generated\n");
			String fileName = "spoonClustering.dot";
			Utils.generateFile(fileName, graph1.toString());
			
		}
		
		else {
			System.out.println("dandro graph is too large, can't be shown");
		}
		
		System.out.println("results are stored in results\\MergeSteps.txt");
		Utils.generateFile("results\\MergeSteps.txt", mergeLogger.toString());
	}
	public static void question1withoutshowing() throws IOException{
		Set<String> classes = new LinkedHashSet<String>(classNames);
		mergeLogger.append(combinaisonStats + "\n");
		// Setup dendro Nodes
		combinaisonStats.keySet().forEach(element -> {
			nodeElement.put(element, new Node<List<String>>(List.of(element.get(0)), 0));
		});

		do {

			mergeWithMaxCombinaisonCall();
			
		} while (combinaisonStats.size()!=1);
		combinaisonStats.forEach((key, value) -> {
			root = nodeElement.get(key);
		});
		
		if(showGraph) {
			treeToString(root);
			treatinggraph();
			if(!graph1.contains("}"))
				graph1 += "}";
			
		}		
	}

	public static List<List<String>> mergeWithMaxCombinaisonCall() {
		List<List<String>> list = new ArrayList<List<String>>();
		List<List<String>> combo = new ArrayList<List<String>>();
		List<String> c1 = new ArrayList<String>();
		List<String> c2 = new ArrayList<String>();
		int sum = 0;
		int max = 0;
		for (List<String> combinaisons : combinaisonStats.keySet()) {
			list.add(combinaisons);
		}
		for (int i = 0; i < list.size(); i++) {

			for (int j = 0; j < list.get(i).size(); j++) {
				String className1 = list.get(i).get(j);
				if (list.size() >= 2) {
					for (int k = i + 1; k < list.size(); k++) {
						if (list.get(i).size() == 1)
							sum = 0;
						for (int l = 0; l < list.get(k).size(); l++) {
							String className2 = list.get(k).get(l);

							// System.out.println(getNumberCallsOfTwoClasses(className1, className2));
							sum += (getNumberCallsOfTwoClasses(className1, className2)[0]
									+ getNumberCallsOfTwoClasses(className1, className2)[1]);
						
						}
						if (max <= sum) {
							combo = new ArrayList<List<String>>();
							max = sum;
							c1 = list.get(i);
							combo.add(c1);
							c2 = list.get(k);
							combo.add(c2);

						}
					}

				}

			}

		}
		// merge

		List<String> l = new ArrayList<String>();
		for (List<String> lS : combo) {

			for (String c : lS) {
				if (!l.contains(c))
					l.add(c);
			}
		}
		
		int couplageMetric = somme(c1, c2);
		Node parent = new Node<List>(l,couplageMetric);
		Node nodeC1 = nodeElement.get(c1);
		Node nodeC2 = nodeElement.get(c2);
		parent.addChild(nodeC1);
		parent.addChild(nodeC2);
		nodeElement.put(l, parent);
		combinaisonStats.put(l, couplageMetric);
		combinaisonStats.remove(c1);
		combinaisonStats.remove(c2);
		nodeElement.remove(c1);
		nodeElement.remove(c2);

		mergeLogger.append(combinaisonStats + "\n");
		return combo;
	}
	
	public static int somme(List<String> c1, List<String> c2) {
        int s = 0;
        for(String className1 : c1) {
            for(String className2: c2) {
                int stats[] = getNumberCallsOfTwoClasses(className1, className2);
                s += stats[0]+stats[1];
            }
        }

        return s;
    }


	private static <T> void treeToString(Node<T> node) {
		tree+= "\t"+ node.getData()+" metric "+ node.getNodeMetric()+"\n";
		if(node.getNodeMetric()!=0) {
			
			metrique = node.getNodeMetric();
			counter = 0;
			((ArrayList<String>) node.getData()).forEach(child -> 
			{


				if (counter%2==0 && !graph1.contains("[style = filled,color = chartreuse, label="+child+", shape = rectangle]")) {// && ((!graph1.contains("node"+counter + " -- node" + (counter+1)) && !graph1.contains("node"+(counter+1) + " -- node" + (counter+3))) 

					if(counter==0) {
						graph1+="node"+(counter)+"[style = filled,color = chartreuse, label="+child+", shape = rectangle]\n";

						graph1 += "node"+counter + " -- node" + (counter+1)+"\n"; 
						graph1 += "node"+(counter+1) + " -- node" + (counter+3)+"\n";
						last_line_of_graph="node"+(counter+1) + " -- node" + (counter+3);
						counter++;
					}
					else {
						graph1+="node"+(counter+2)+"[style = filled,color = chartreuse, label="+child+", shape = rectangle]\n";

						graph1 += "node"+(counter+2) + " -- node" + (counter+1)+"\n"; 
						graph1 += "node"+(counter+1) + " -- node" + (counter+3)+"\n";
						last_line_of_graph="node"+(counter+1) + " -- node" + (counter+3);
						counter++;
					}
					
				}
				else {		

					if(counter== 1 && !graph1.contains("[style = filled,color = chartreuse, label="+child+", shape = rectangle]")) {
						graph1+="node"+(counter+1)+"[style = filled,color = chartreuse, label="+child+", shape = rectangle]\n";
						graph1 += "node"+(counter+1) + " -- node" + (counter)+"\n";
						last_line_of_graph="node"+(counter+1) + " -- node" + (counter)+"\n";
						counter++;
					}
					else {

						if(counter%2==1 && !graph1.contains("[style = filled,color = chartreuse, label="+child+", shape = rectangle]")) {

							graph1+="node"+(counter+3)+"[style = filled,color = chartreuse, label="+child+", shape = rectangle]\n";
							graph1 += "node"+(counter+3) + " -- node" + (counter+2)+"\n";
							graph1 += "node"+(counter+2) + " -- node" + (counter+4)+"\n";
							last_line_of_graph="node"+(counter+2) + " -- node" + (counter+4)+"\n";
							counter+=2;
						}
					}
					
					
				}
			});
		}

		node.getChildren().forEach(each -> treeToString(each));
	}
	
	public static void treatinggraph(){
		String[] nodes = last_line_of_graph.split("--");
		String node = nodes[1].trim();
	    int index = 0;
	    int count = 0 ; 
		while ((index = graph1.indexOf(node, index)) != -1) {
	         count++;
	         index = index + node.length();
	     }
		if (count == 1) {
			String[] lines_of_graph = graph1.split("\n");

			lines_of_graph = ArrayUtils.remove(lines_of_graph, lines_of_graph.length-1);
			String[] ll = lines_of_graph[lines_of_graph.length-1].split("--");
			String[] ll_ = lines_of_graph[lines_of_graph.length-3].split("--");
			ll[1]= Integer.toString(metrique);
			ll_[1] = Integer.toString(metrique);
			String new_last_line = ll[0] + " -- "+ll[1];
			String new_before_last_line = ll_[0]+" -- "+ll_[1];
			lines_of_graph[lines_of_graph.length-1] = new_last_line;
			lines_of_graph[lines_of_graph.length-3] = new_before_last_line;
			graph1 = "";
			for (int i = 0 ; i < lines_of_graph.length; i++) {
				graph1+=lines_of_graph[i]+"\n";
			}

		}
		
		
	}

	public static void main(String args[]) {
		BufferedReader inputReader;
		try {
			inputReader = new BufferedReader(new InputStreamReader(System.in));
			if (args.length < 1) {
				setTestProjectPath(inputReader);
			}
			else {
				verifyTestProjectPath(inputReader, args[0]);
			}
			String userInput = "";
			do {
				menu();
				userInput = inputReader.readLine();
				processUserInput(userInput);
//				Thread.sleep(3000);

			} while (!userInput.equals(QUIT));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	protected static void verifyTestProjectPath(BufferedReader inputReader, String userInput) throws IOException {
		File projectFolder = new File(userInput);

		while (!projectFolder.exists() || !userInput.endsWith("src")) {
			System.err.println("Error: " + userInput + " either doesn't exist or isn't a java project src/ folder. "
					+ "Please try again: ");
			userInput = inputReader.readLine();
			projectFolder = new File(userInput);
		}

		TEST_PROJECT_PATH = userInput;
	}
}
