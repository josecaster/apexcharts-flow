package sr.we.ui.views.password;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.CustomErrorHandler;
import sr.we.data.controller.UserService;
import sr.we.ui.components.EmailAddress;
import sr.we.ui.views.login.IntroView;
import sr.we.ui.views.login.LoginView;

import java.util.Optional;

@Route(value = "forgot-password/:random")
//@AnonymousAllowed
public class ForgotPassword extends VerticalLayout implements BeforeEnterObserver {


    private String random;

    public ForgotPassword() {
        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());

        setSpacing(false);

        VerticalLayout verticalLayout = new VerticalLayout();
//        verticalLayout.getThemeList().add("dark");

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        verticalLayout.add(img);


        verticalLayout.add(new H4("Reset password"));

        EmailAddress emailField = new EmailAddress();
        emailField.setWidthFull();
        PasswordField passwordField = new PasswordField();
        passwordField.setWidthFull();
        passwordField.setLabel("New password");
        passwordField.setPlaceholder("Password");
        passwordField.setHelperText("A password must be at least 8 characters. It has to have at least one letter and one digit.");
        passwordField.setPattern("^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$");
        passwordField.setErrorMessage("Not a valid password");

        PasswordField passwordCField = new PasswordField();
        passwordCField.setWidthFull();
        passwordCField.setLabel("Confirm password");
        passwordCField.setPlaceholder("Password");
        passwordCField.setHelperText("A password must be at least 8 characters. It has to have at least one letter and one digit.");
        passwordCField.setPattern("^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$");
        passwordCField.setErrorMessage("Not a valid password");

        verticalLayout.add(new FormLayout(emailField, passwordField, passwordCField));

        Button registerBtn = new Button("Reset");
        registerBtn.setWidthFull();
        registerBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        registerBtn.addClickShortcut(Key.ENTER);
        verticalLayout.add(registerBtn);

        setSizeFull();
//        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        verticalLayout.add(new Hr());

        Button button = new Button();
        button.setText("Home");
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        button.addClickListener(f -> UI.getCurrent().navigate(IntroView.class));

        Button go_to_login = new Button("Login");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        go_to_login.addClickListener(f -> UI.getCurrent().navigate(LoginView.class));

        verticalLayout.setMaxWidth("350px");
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        verticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        verticalLayout.getStyle().set("text-align", "center");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.END);
        headerLayout.add(button, go_to_login);
        add(headerLayout, verticalLayout);

        registerBtn.addClickListener(f -> {
            if (emailField.isInvalid()) {
                return;
            }
            if (passwordCField.isInvalid()) {
                return;
            }

            if (passwordField.isInvalid()) {
                return;
            }

            UserService userService = ContextProvider.getBean(UserService.class);

            UserService.Auth vo = new UserService.Auth();
            vo.confirmPassword = passwordCField.getValue();
            vo.username = emailField.getValue();
            vo.password = passwordField.getValue();
            vo.temp = random;
            userService.reset(vo);
            Notification.show("Password reset done");
            UI.getCurrent().navigate(LoginView.class);

        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> random1 = event.getRouteParameters().get("random");
        if (random1.isPresent()) {
            random = random1.get();
            if (StringUtils.isBlank(random)) {
                event.forwardTo(IntroView.class);
            }
        } else {
            event.forwardTo(IntroView.class);
        }
    }
}
