package sr.we.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;

import java.security.Principal;
import java.util.Optional;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import sr.we.ContextProvider;
import sr.we.data.controller.UserService;
import sr.we.shekelflowcore.entity.ThisUser;

@Component
public class AuthenticatedUser {

    private final UserService userService;

    @Autowired
    public AuthenticatedUser(UserService userService) {
        this.userService = userService;
    }

    private static Optional<Principal> getAuthentication() {
        VaadinServletRequest vaadinServletRequest = VaadinServletRequest.getCurrent();
        Principal userPrincipal = vaadinServletRequest.getUserPrincipal();
        return Optional.ofNullable(userPrincipal)
                .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken));
    }

    public static Optional<ThisUser> get() {
        return getAuthentication().map(authentication -> {
            if(authentication instanceof UsernamePasswordAuthenticationToken){
                return (ThisUser)((UsernamePasswordAuthenticationToken)authentication).getPrincipal();
            }
            return (ThisUser) authentication;
        });
    }

    public static String token(){
        AuthenticatedUser user = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> thisUser = user.get();
        if(thisUser.isPresent()){
            return thisUser.get().getToken().getToken();
        }
        return null;
    }

    public static void logout() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
//        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
//        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

}
