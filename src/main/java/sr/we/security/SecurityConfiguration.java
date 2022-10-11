package sr.we.security;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

    public static final String LOGOUT_URL = "/";

    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String s = requestURL.toString();
        if (s.endsWith("sw-runtime-resources-precache.js")) {
            return true;
        }
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null && Stream.of(HandlerHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        SecurityContextHolder.setStrategyName(VaadinAwareSecurityContextHolderStrategy.class.getName());
//        CsrfConfigurer var3 = http.csrf();
//        var3.disable();
//        http.requestCache().requestCache(new CustomRequestCache());
//        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry = http.authorizeRequests();
//        urlRegistry.requestMatchers(SecurityConfiguration::isFrameworkInternalRequest).permitAll();;//.permitAll();
//        urlRegistry.antMatchers("/forgot-password/**").permitAll();
//        urlRegistry.anyRequest().authenticated();
//
//        setLoginView(http, LoginView.class, LOGOUT_URL);
//        http.formLogin().defaultSuccessUrl("/u");
//        http.csrf().disable();
        // Vaadin handles CSRF internally
        http.csrf().disable()

                // Register our CustomRequestCache, which saves unauthorized access attempts, so the user is redirected after login.
                .requestCache().requestCache(new CustomRequestCache())

                // Restrict access to our application.
                .and().authorizeRequests()

                // Allow all Vaadin internal requests.
                .requestMatchers(SecurityConfiguration::isFrameworkInternalRequest).permitAll()
                .and().authorizeRequests().antMatchers("/forgot-password/**").permitAll()
                .and().authorizeRequests().antMatchers("/invoice/**").permitAll()
                .and().authorizeRequests().antMatchers("/").permitAll()

                // Allow all requests by logged-in users.
                .anyRequest().authenticated()

                // Configure the login page.
                .and().formLogin()
                .loginPage("/login").permitAll()
//                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/u")
                .failureUrl("/login?error")

                // Configure logout
                .and().logout().logoutSuccessUrl("/u");
//
//        super.configure(http);
//        setLoginView(http, LoginView.class);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // Client-side JS
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // the robots exclusion standard
                "/robots.txt",

                // web application manifest
                "/manifest.webmanifest",
                "/sw.js",
                "/offline.html",
                "/my-lumo.html",
                // icons and images
                "/icons/**",
                "/images/**",
                "/styles/**",

                // (development mode) H2 debugging console
                "/h2-console/**");
//        web.ignoring().antMatchers("/images/*.png");
    }

    @Override
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider());
    }
}
