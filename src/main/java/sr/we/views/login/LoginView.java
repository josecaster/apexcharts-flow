package sr.we.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.Token;
import sr.we.views.about.AboutView;

import java.util.Optional;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeLeaveObserver {
    public LoginView() {
        setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("ShekelFlow");
        i18n.getHeader().setDescription("Built with ♥ by José Caster");
        i18n.setAdditionalInformation("Please, contact administrator if you're experiencing issues logging into your account");
        i18n.getForm().setForgotPassword("Return Home! | Forgot Password?");
        setI18n(i18n);


        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(f -> {
            UI.getCurrent().navigate(IntroLayout.class);
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
}
