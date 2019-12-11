package dima.docker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import dima.docker.dto.ContainerInfoDTO;

public class InfoReader {

	public List<ContainerInfoDTO> readDocker() {
		ProcessBuilder processBuilder = new ProcessBuilder();

		// -- Linux --

		// Run a shell command
		String results = executeCommand("docker", "ps");
		System.out.println(results);
		return null;
	}

	private String executeCommand(String... params) {
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
