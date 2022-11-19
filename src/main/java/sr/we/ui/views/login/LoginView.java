package sr.we.ui.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.SpringVaadinSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import sr.we.ContextProvider;
import sr.we.CustomErrorHandler;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.Token;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeLeaveObserver, AfterNavigationObserver {
    public LoginView() {
        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());
        setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Header header = new LoginI18n.Header();
        i18n.setHeader(header);
        i18n.getHeader().setTitle("ShekelFlow");
        Header header2 = new Header();
        Image seaqns_icon = new Image("images/seaqns-icon.png", "seaqns icon");
        seaqns_icon.setWidth("100px");
        header2.add(seaqns_icon);
        setTitle(header2);

        i18n.getHeader().setDescription("Built with ♥");
        i18n.setAdditionalInformation("Please, contact administrator if you're experiencing issues logging into your account");
        i18n.getForm().setForgotPassword("Return Home! | Forgot Password?");
        setI18n(i18n);


        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(f -> {
            UI.getCurrent().navigate(IntroView.class);
            if(isOpened()){
                setOpened(false);
            }
        });
        setOpened(true);

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> thisUser = bean.get();
        if(!thisUser.isPresent()){
            return;
        }
        ThisUser thisUser1 = thisUser.get();
        Token token = thisUser1.getToken();
        SpringVaadinSession.getCurrent().setAttribute("Token",token.getToken());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        Map<String, List<String>> parameters = event.getLocation().getQueryParameters().getParameters();
        boolean isError = parameters.containsKey("error");
        if (isError) {
            VaadinServletRequest vsr = VaadinServletRequest.getCurrent();
            HttpServletRequest req = vsr.getHttpServletRequest();
            javax.servlet.http.HttpSession sess = req.getSession();
            AuthenticationException ex = (AuthenticationException) sess.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex == null) {
            }
            else {
                Notification.show(ex.getMessage());
                sess.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);// already reported so clear
            }
        }
        boolean isVerif = parameters.containsKey("verif");
        if (isVerif) {
            String verify = parameters.get("verif").get(0);
            SpringVaadinSession.getCurrent().setAttribute("Verify",verify);
        } else {
            SpringVaadinSession.getCurrent().setAttribute("Verify",null);
        }
    }
}
