package sr.we.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import sr.we.ContextProvider;
import sr.we.data.service.UserRepository;
import sr.we.shekelflowcore.entity.ThisUser;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String name = authentication.getName();
        // You can get the password here
        String password = authentication.getCredentials().toString();
        userRepository = ContextProvider.getBean(UserRepository.class);
            ThisUser byUsername = userRepository.findByUsername(name, password);
            Authentication auth = new UsernamePasswordAuthenticationToken(byUsername,
                    password, byUsername.getAuthorities());
            return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
