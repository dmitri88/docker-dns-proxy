package dima.listener;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dima.context.ContainerStatus;
import dima.docker.ApplicationContext;
import dima.docker.dto.ContainerInfoDTO;
import dima.event.ContainerStatusChangeEvent;
import dima.event.DockerStatusChangeEvent;
import dima.event.ScannedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContainerLifecycleProducer implements IDockerListenable{

	private Set<String> runningContainers =  new HashSet<>();
	
	@Override
	public void onScanned(ScannedEvent event) {
		List<ContainerInfoDTO> newContainers = event.getContainers();
		Map<String,ContainerInfoDTO> containers =  newContainers.stream().collect(
                Collectors.toMap(x -> x.getName(), x -> x));
		Set<String> prevContainers = new HashSet<>(runningContainers);
		Set<String> removedContainers = new HashSet<>(runningContainers);
		Set<String> createdContainers;

		synchronized (this) {
			runningContainers.clear();
			runningContainers.addAll(containers.keySet());			
		
			removedContainers.removeAll(containers.keySet());
			createdContainers = new HashSet<>(containers.keySet());
			
			createdContainers.removeAll(prevContainers);
			
			if(createdContainers.size()>0 || removedContainers.size()>0) {
				ApplicationContext.fireEvent(new DockerStatusChangeEvent(createdContainers,removedContainers,runningContainers));
			}
		}
		
		for(String changed: createdContainers ) {
			ApplicationContext.fireEvent(new ContainerStatusChangeEvent(changed,ContainerStatus.STARTED,containers.get(changed)));			
		}
		
		for(String changed:removedContainers) {
			ApplicationContext.fireEvent(new ContainerStatusChangeEvent(changed,ContainerStatus.STOPPED,null));
			
		}
	}

}
