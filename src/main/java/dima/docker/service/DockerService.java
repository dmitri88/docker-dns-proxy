package dima.docker.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import dima.docker.ApplicationContext;
import dima.docker.dto.ContainerInfoDTO;
import dima.event.ScanEvent;
import dima.event.ScannedEvent;
import dima.utils.CommandLineTools;

public class DockerService {
	
	@SuppressWarnings("unused")
	public void scanDockerForChanges(Runnable callback) throws Exception {
		Runtime rt = Runtime.getRuntime();
		String[] commands = {"docker", "events"};
		Process proc = rt.exec(commands);

		BufferedReader stdInput = new BufferedReader(new 
		     InputStreamReader(proc.getInputStream()));

		// Read the output from the command
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			ApplicationContext.fireEvent(new ScanEvent());
			callback.run();
		}
	} 
	
	
	public List<ContainerInfoDTO> readDocker() throws JsonMappingException, JsonProcessingException {
		// Run a shell command
		String stdoutResults = CommandLineTools.executeCommand("docker", "ps");
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
		
		ApplicationContext.fireEvent(new ScannedEvent(results));
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
		String content = CommandLineTools.executeCommand("docker", "inspect",contName);
		ObjectMapper mapper = new ObjectMapper();
		//System.out.println(content);
		List<JsonNode> objects = mapper.readValue(content, new TypeReference<List<JsonNode>>() {
		});
		ContainerInfoDTO result = new ContainerInfoDTO();
		result.setName(contName);
		
		JsonNode networksNode = objects.get(0).get("NetworkSettings").get("Networks");
		TextNode ipNode = (TextNode)networksNode.get(networksNode.fieldNames().next()).get("IPAddress");
		result.getNetworkSettings().setIp(ipNode.asText());
		
		TextNode dirNode = ((TextNode)objects.get(0).get("GraphDriver").get("Data").get("MergedDir"));
		result.getGraphDriver().setMerged(dirNode.asText());
		
		return result;
	}


	public String getVersion() {
		String content = CommandLineTools.executeCommand("docker", "-v");
		return content.replace("Docker version ", "").split(",")[0];
	}
	
}
