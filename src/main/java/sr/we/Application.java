package sr.we;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class, SecurityAutoConfiguration.class})
//@NpmPackage(value = "@fontsource/poppins", version = "4.5.0")
@NpmPackage(value = "@fontsource/rubik", version = "4.5.11")
@Theme(value = "shekelflow")
@PWA(name = "ShekelFlow", shortName = "ShekelFlow", offlineResources = {})
@Push
@NpmPackage(value = "line-awesome", version = "1.3.0")
@EnableConfigurationProperties(ConfigProperties.class)
//@Inline(wrapping = Inline.Wrapping.AUTOMATIC,
//        position = Inline.Position.APPEND,
//        target = TargetElement.BODY,
//        value = "splash-screen.html")
//@EnableVaadin("sr.we.ui")
@EnableAutoConfiguration
@ComponentScan
public class Application extends SpringBootServletInitializer implements AppShellConfigurator, VaadinServiceInitListener {

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

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.setViewport("width=device-width, initial-scale=1");
        settings.setPageTitle("SeaQns-Finance");
        settings.setBodySize("100vw", "100vh");
        settings.addMetaTag("author", "Blackhammer");
//        settings.addFavIcon("icon", "icons/icon-192.png", "192x192");
//        settings.addLink("shortcut icon", "icons/favicon.ico");

//        settings.addInlineFromFile(
//                TargetElement.BODY,
//                Inline.Position.APPEND,
//                "splash-screen.html",
//                Inline.Wrapping.NONE);
//        settings.addInlineWithContents(Inline.Position.PREPEND,
//                "console.log(\"foo\");", Inline.Wrapping.JAVASCRIPT);
    }

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        /*serviceInitEvent.getSource().addUIInitListener(uiInitEvent -> {
            LoadingIndicatorConfiguration conf = uiInitEvent.getUI().getLoadingIndicatorConfiguration();

            // disable default theme -> loading indicator isn't shown
            conf.setApplyDefaultTheme(false);

            *//*
         * Delay for showing the indicator and setting the 'first' class name.
         *//*
            conf.setFirstDelay(300); // 300ms is the default

            *//* Delay for setting the 'second' class name *//*
            conf.setSecondDelay(1500); // 1500ms is the default

            *//* Delay for setting the 'third' class name *//*
            conf.setThirdDelay(5000); // 5000ms is the default
        });*/
    }

}
