package dima.listener;

import dima.event.ContainerStatusChangeEvent;
import dima.utils.CommandLineTools;

public class AutomountListener implements IDockerListenable{

	@Override
	public void onContainerStart(ContainerStatusChangeEvent event) {
		String path = "/media/docker/"+event.getName();
		CommandLineTools.executeCommand("mkdir","-p",path);		
		//CommandLineTools.executeCommand("mount","--bind",event.getInfo().getGraphDriver().getMerged(),path);
		
	}

	@Override
	public void onContainerStop(ContainerStatusChangeEvent event) {
		String path = "/media/docker/"+event.getName();
		CommandLineTools.executeCommand("umount",path);
		//CommandLineTools.executeCommand("rmdir",path);
	}

}
