package dima.docker.dto;

import lombok.Data;

@Data
public class ContainerInfoDTO {
	private String name;
	private NetworkSettingsDTO networkSettings = new NetworkSettingsDTO();
	private GraphDriverDTO graphDriver = new GraphDriverDTO();

	@Data
	public static class GraphDriverDTO {
		private String merged;
	} 
}
