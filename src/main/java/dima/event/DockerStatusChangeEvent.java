package dima.event;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DockerStatusChangeEvent extends DockerEvent {

	private final Set<String> created;
	private final Set<String> removed;
	private final Set<String> running;

	public DockerStatusChangeEvent(Set<String> created, Set<String> removed,
			Set<String> running) {
		this.created = created;
		this.removed = removed;
		this.running = running;
	}

}
