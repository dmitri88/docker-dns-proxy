package dima.event;

import dima.context.ContainerStatus;
import dima.docker.dto.ContainerInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
public class ContainerStatusChangeEvent extends DockerEvent{
	private String name;
	private ContainerStatus status;
	private ContainerInfoDTO info;
}
