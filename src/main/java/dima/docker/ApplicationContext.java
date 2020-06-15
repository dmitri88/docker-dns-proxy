package dima.docker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dima.context.ContainerStatus;
import dima.event.ContainerStatusChangeEvent;
import dima.event.DockerEvent;
import dima.event.DockerStatusChangeEvent;
import dima.event.ScanEvent;
import dima.event.ScannedEvent;
import dima.listener.IDockerListenable;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ApplicationContext {
	
	@Getter
	private static ApplicationContext instance = new ApplicationContext();
	
	private String hostsFilePath;
	private String dockerVersion;
	
	private static final List<IDockerListenable> listeners = Collections.synchronizedList(new ArrayList<>()); 

	private ApplicationContext() {
	}

	public static void fireEvent(DockerEvent event) {
		log.trace("Received event: "+ event);
		for(IDockerListenable listener: listeners) {
			if(event instanceof ScanEvent) {
				listener.onScan((ScanEvent) event);
			}
			else if(event instanceof ScannedEvent) {
				listener.onScanned((ScannedEvent) event);
			}
			else if(event instanceof DockerStatusChangeEvent) {
				listener.onDockerStatusChange((DockerStatusChangeEvent) event);
			}
			else if(event instanceof ContainerStatusChangeEvent) {
				ContainerStatusChangeEvent event1 = (ContainerStatusChangeEvent) event;
				if(event1.getStatus() == ContainerStatus.STARTED)
					listener.onContainerStart(event1);
				else
					listener.onContainerStop(event1);
			}
		}
		
	}
	
	public static void addEventListener(IDockerListenable listener) {
		listeners.add(listener);
	}
}
