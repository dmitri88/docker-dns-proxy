package dima.docker;

import lombok.Data;
import lombok.Getter;

@Data
public class ApplicationContext {
	
	@Getter
	private static ApplicationContext instance = new ApplicationContext();
	
	private String hostsFilePath;
	private String dockerVersion;

	private ApplicationContext() {
	}
}
