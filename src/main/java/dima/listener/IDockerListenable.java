package dima.listener;

import dima.event.ContainerStatusChangeEvent;
import dima.event.DockerStatusChangeEvent;
import dima.event.ScanEvent;
import dima.event.ScannedEvent;

public interface IDockerListenable {

	default public void  onScan(ScanEvent event) {
		
	}
	
	default public void  onScanned(ScannedEvent event) {
		
	}
	
	default public void  onDockerStatusChange(DockerStatusChangeEvent event) {
		
	}
	
	default public void  onContainerStart(ContainerStatusChangeEvent event) {
		
	}
	
	default public void  onContainerStop(ContainerStatusChangeEvent event) {
		
	}
}
