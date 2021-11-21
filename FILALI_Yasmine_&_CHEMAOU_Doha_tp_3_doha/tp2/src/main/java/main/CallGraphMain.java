package main;
/** Auteurs : 
*	@author Yasmine FILALI 
*	@author Doha CHEMAOU
*/


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Scanner;

import graphs.CallGraph;
import graphs.StaticCallGraph;
import metrics.*;
import clusters.*;
import partition.*;

import processors.ASTProcessor;
public class CallGraphMain extends AbstractMain {

	@Override
	protected void menu() {
		StringBuilder builder = new StringBuilder();
		builder.append("\t\t -----------------------------------Menu------------------------------------\n");
		builder.append("\t\t|1. Static call graph                                                       |");
		builder.append("\n\t\t|2. Métrique de couplage entre chaque deux classes                          |");
		builder.append("\n\t\t|3. Métrique de couplage pour deux classes spécifiques                      |");
		builder.append("\n\t\t|4. Générez un graphe de couplage pondéré entre les classes de l'application|");
		builder.append("\n\t\t|5. regroupement hiérarchique (clustering)                                  |");
		builder.append("\n\t\t|6. identification de groupe de classes couplées                            |");
		builder.append("\n\t\t|" + QUIT + ". To quit                                                                 |");
		builder.append("\n\t\t ---------------------------------------------------------------------------");

		System.out.println(builder);
	}

	public static void main(String[] args) throws ParseException {
		CallGraphMain main = new CallGraphMain();
		BufferedReader inputReader;
		CallGraph callGraph = null;
		try {
			inputReader = new BufferedReader(new InputStreamReader(System.in));
			if (args.length < 1)
				setTestProjectPath(inputReader);
			else
				verifyTestProjectPath(inputReader, args[0]);
			String userInput = "";
			do {
				main.menu();
				userInput = inputReader.readLine();
				main.processUserInput(userInput, callGraph);
				Thread.sleep(3000);

			} while (!userInput.equals(QUIT));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	protected void processUserInput(String userInput, ASTProcessor processor) throws ParseException {
		CallGraph callGraph = (CallGraph) processor;
		Scanner sc = new Scanner(System.in);
		try {
			callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
			switch (userInput) {
			case "1": {
				callGraph.log();
				break;
			}

			case "2": {
				Metrique.affiche_metrique_couplage(callGraph);
				break;
			}
			case "3": {
				Metrique.metrique_between_two_specific_classes(sc,callGraph);
				break;
			}

			case "4":{
				Graph.dotGraph(callGraph);
				break;
			}

			case "5":{
				Clustering.clustering(callGraph);
				return;
			}
			case "6":{
				Partition.partition(callGraph);
				return;
			}

			case QUIT:
				System.out.println("Bye...");
				return;

			default:
				System.err.println("\u001B[31mSorry, wrong input. Please try again.\u001B[0m");
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
