package dima.docker.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import dima.docker.dto.ContainerInfoDTO;

public class DockerService {
	@SuppressWarnings("unused")
	public void scanDockerForChanges(Runnable callback) throws Exception {
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"docker", "events"};
		Process proc = rt.exec(commands);

		BufferedReader stdInput = new BufferedReader(new 
		     InputStreamReader(proc.getInputStream()));

//		BufferedReader stdError = new BufferedReader(new 
//		     InputStreamReader(proc.getErrorStream()));

		// Read the output from the command
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			callback.run();
		}
	} 
	
	
	public List<ContainerInfoDTO> readDocker() throws JsonMappingException, JsonProcessingException {
		// Run a shell command
		String stdoutResults = executeCommand("docker", "ps");
		String[] lines = stdoutResults.split("\n");
		if(lines.length<1 || !lines[0].contains("NAMES")) {
			throw new IllegalStateException("docker not found");
		}
		List<ContainerInfoDTO> results = new ArrayList<>();
		for(int i=1;i<lines.length;i++) {
			ContainerInfoDTO info = parseLine(lines[i]);
			if(info != null) {
				results.add(info);
			}
			
		}
		return results;
	}

	private ContainerInfoDTO parseLine(String dockerLine) throws JsonMappingException, JsonProcessingException {
		String[] tokens = dockerLine.split("\\s+");
		if(tokens == null || tokens.length<1) {
			return null;
		}
		return inspect(tokens[tokens.length-1]);
	}


	private ContainerInfoDTO inspect(String contName) throws JsonMappingException, JsonProcessingException {
		String content = executeCommand("docker", "inspect",contName);
		ObjectMapper mapper = new ObjectMapper();
		List<JsonNode> objects = mapper.readValue(content, new TypeReference<List<JsonNode>>() {
		});
		ContainerInfoDTO result = new ContainerInfoDTO();
		result.setName(contName);
		
		JsonNode networksNode = objects.get(0).get("NetworkSettings").get("Networks");
		TextNode ipNode = (TextNode)networksNode.get(networksNode.fieldNames().next()).get("IPAddress");
		result.setIp(ipNode.asText());
		return result;
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


	public String getVersion() {
		String content = executeCommand("docker", "-v");
		return content.replace("Docker version ", "").split(",")[0];
	}
	
}
