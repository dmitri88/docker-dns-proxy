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

import dima.context.Constants;
import dima.docker.dto.ContainerInfoDTO;
import dima.docker.service.DockerService;
import dima.event.ContainerStatusChangeEvent;
import dima.event.DockerStatusChangeEvent;
import dima.event.ScanEvent;
import dima.event.ScannedEvent;
import dima.listener.AutomountListener;
import dima.listener.ContainerLifecycleProducer;
import dima.listener.IDockerListenable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
	
	@Getter
	private DockerService dockerService = new DockerService();

	public static void main(String[] args) throws Exception {
		log.info("Started docker-watcher "+Constants.VERSION);
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
        application.registerContext();
        
        context.setHostsFilePath(inputFilePath);
        context.setDockerVersion(application.getDockerService().getVersion());
        
        application.updateHosts();
        application.getDockerService().scanDockerForChanges(()-> application.updateHosts());
	}
	
	private void registerContext() {
		ApplicationContext.addEventListener(new IDockerListenable() {

			@Override
			public void onScan(ScanEvent event) {
				log.debug("Received event: ScanEvent");
			}

			@Override
			public void onScanned(ScannedEvent event) {
				log.debug("Received event: ScannedEvent");
			}

			@Override
			public void onDockerStatusChange(DockerStatusChangeEvent event) {
				log.debug("Received event: "+ event);
			}

			@Override
			public void onContainerStart(ContainerStatusChangeEvent event) {
				log.debug("Received event: "+ event);
			}

			@Override
			public void onContainerStop(ContainerStatusChangeEvent event) {
				log.debug("Received event: "+ event);
			}
			
		});
		ApplicationContext.addEventListener( new ContainerLifecycleProducer());
		ApplicationContext.addEventListener( new AutomountListener());
		
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
