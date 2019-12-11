package dima.docker;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import dima.docker.dto.ContainerInfoDTO;
import dima.docker.service.DockerService;
import lombok.Getter;

public class Application {
	
	@Getter
	private DockerService dockerService = new DockerService();

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		
		Option hostsfile = new Option("h", "hostsfile", true, "hosts file path");
		options.addOption(hostsfile);
		
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("dns-proxy", options);

            System.exit(1);
            return;
        }		
        
        String inputFilePath = cmd.getOptionValue("hostsfile");
        if(inputFilePath == null) {
        	inputFilePath = "/etc/hosts";
        }
		
        Application application = new Application();
        ApplicationContext context = ApplicationContext.getInstance();
        
        context.setHostsFilePath(inputFilePath);
        context.setDockerVersion(application.getDockerService().getVersion());
        
        application.updateHosts();
        application.getDockerService().scanDockerForChanges(()-> application.updateHosts());
	}

	private void updateHosts()  {
		try {
			List<ContainerInfoDTO> list = dockerService.readDocker();
			new HostsUpdater().update(list);
		} catch (IOException  e) {
			throw new RuntimeException(e);
		}
	}
}
