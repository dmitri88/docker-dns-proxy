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
	private static String DNS_PROXY_COMMENT = "#dns-proxy auto added";
	
	public void update(List<ContainerInfoDTO> items) throws IOException {
		List<String> currentLines = readHosts();
		currentLines = updateHosts(currentLines,items);
		writeHosts(currentLines);
	}
	
	private List<String> updateHosts(List<String> currentLines, List<ContainerInfoDTO> items) {
		List<String> filtered = currentLines.stream().filter(s -> !s.contains(DNS_PROXY_COMMENT)).collect(Collectors.toList());
		
		for(ContainerInfoDTO info:items) {
			filtered.add(info.getIp()+"\t"+info.getName()+"\t"+DNS_PROXY_COMMENT);
		}
		return filtered;
	}

	private void writeHosts(List<String> currentLines) throws IOException {
		String fileName = ApplicationContext.getInstance().getHostsFilePath();
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) 
		{
			for(String line:currentLines) {
				writer.write(line +"\n");	
			}
		}
	}

	private List<String> readHosts() throws IOException {
		String fileName = ApplicationContext.getInstance().getHostsFilePath();

		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			return stream.collect(Collectors.toList());
		} 
	}
}
