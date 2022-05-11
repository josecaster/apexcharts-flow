package sr.we;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@NpmPackage(value = "@fontsource/poppins", version = "4.5.0")
@Theme(value = "shekelflow")
@PWA(name = "ShekelFlow", shortName = "ShekelFlow", offlineResources = {})
@Push
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static final String CUSTOM_BUNDLE_PREFIX = "custom_messages";
    public static void main(String[] args) {

//        SpringApplication.run(Application.class, args);
        new SpringApplicationBuilder(Application.class)
                .properties("spring.config.name:application", "spring.config.location:classpath:/,file:./").build()
                .run(args);

        new ApplicationHome(Application.class).getDir();
        TranslationProvider bean = ContextProvider.getBean(TranslationProvider.class);
        bean.addCustom(CUSTOM_BUNDLE_PREFIX);
    }

}
