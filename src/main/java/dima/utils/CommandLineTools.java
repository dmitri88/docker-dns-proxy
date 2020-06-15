package dima.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineTools {
	
	public static String executeCommand(String... params) {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(params);

		try {

			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				return output.toString();
			} else {
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
