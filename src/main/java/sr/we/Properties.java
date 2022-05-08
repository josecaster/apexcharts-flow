package sr.we;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

@Component
public class Properties {

	@Autowired
	BuildProperties buildProperties;

	@PostConstruct
	private void logVersion() {
		System.out.println(buildProperties.getName());
		System.out.println(buildProperties.getVersion());
		System.out.println(buildProperties.get("time"));
		System.out.println(buildProperties.getGroup());
	}

}
