package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import processors.Processor;

public abstract class AbstractMain {
	public static String TEST_PROJECT_PATH;
	public static final String QUIT = "0";

	protected static void setTestProjectPath(BufferedReader inputReader) throws IOException {
		File projectFolder;

		System.out.println("\u001B[34m\n\nPlease provide the path to a java project's src/ folder: \u001B[0m");
		TEST_PROJECT_PATH = inputReader.readLine();
		projectFolder = new File(TEST_PROJECT_PATH);

		while (!projectFolder.exists() || !TEST_PROJECT_PATH.endsWith("src/")) {
			System.err.println("\u001B[31mError: " + TEST_PROJECT_PATH
					+ " either doesn't exist or isn't a java project src/ folder. " + "Please try again: \u001B[0m");
			TEST_PROJECT_PATH = inputReader.readLine();
			projectFolder = new File(TEST_PROJECT_PATH);
		}
	}

	protected static void verifyTestProjectPath(BufferedReader inputReader, String userInput) throws IOException {
		File projectFolder = new File(userInput);

		while (!projectFolder.exists() || !userInput.endsWith("src")) {
			System.err.println("\u001B[31mError: " + userInput + " either doesn't exist or isn't a java project src/ folder. "
					+ "Please try again: \u001B[0m");
			userInput = inputReader.readLine();
			projectFolder = new File(userInput);
		}

		TEST_PROJECT_PATH = userInput;
	}

	protected abstract void menu();

	protected void processUserInput(String userInput) {
		System.err.println(
				"processUserInput(String userInput)" + " isn't implemented for " + getClass().getCanonicalName());
	}

	protected void processUserInput(String userInput, Processor<?> processor) {
		System.err.println("processUserInput(String userInput, ASTProcessor processor)" + " isn't implemented for "
				+ getClass().getCanonicalName());
	}

	protected void processUserInput(BufferedReader reader, String userInput, Processor<?> processor) {
		System.err.println("processUserInput(BufferedReader reader, String userInput, ASTProcessor processor)"
				+ " isn't implemented for " + getClass().getCanonicalName());
	}
}
