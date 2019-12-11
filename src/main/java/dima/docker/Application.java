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

public class Application {

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		Option c = new Option("h", "hostsfile", true, "hosts file path");
		
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
        
        String inputFilePath = cmd.getOptionValue("input");
        if(inputFilePath == null) {
        	inputFilePath = "/etc/hosts";
        }
		
		
		List<ContainerInfoDTO> list = new InfoReader().readDocker();
		new HostsUpdater().update(list);
	}
	
}
