package dima.event;

import java.util.List;

import dima.docker.dto.ContainerInfoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
public class ScannedEvent extends DockerEvent {
	
	private final List<ContainerInfoDTO> containers;

}
