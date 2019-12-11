package dima.docker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dima.docker.dto.ContainerInfoDTO;

public class HostsUpdater {
	public void update(List<ContainerInfoDTO> items) throws IOException {
		List<String> hosts = readHosts();
		writeHosts(hosts);
	}
	
	private void writeHosts(List<String> hosts) throws IOException {
		String fileName = "/etc/hosts";
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) 
		{
		    //writer.write("Hello World !!");
		}
	}

	private List<String> readHosts() throws IOException {
		String fileName = "/etc/hosts";

		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			return stream.collect(Collectors.toList());
		} 
	}
}
