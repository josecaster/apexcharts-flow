package sr.we;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
@ConfigurationProperties("sr.we")
public class ConfigProperties {

	private String rest;

	public String getRest() {
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}
}
