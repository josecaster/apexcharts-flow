package sr.we.ui.views.login;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.SpringVaadinSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import sr.we.ContextProvider;
import sr.we.CustomErrorHandler;
import sr.we.data.controller.UserService;
import sr.we.security.CustomAuthenticationProvider;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.vo.UserVO;
import sr.we.ui.components.EmailAddress;
import sr.we.ui.components.MyDialog;
import sr.we.ui.views.person.PersonView;

import java.util.List;
import java.util.Map;

/**
 * The main view is a top-level placeholder for other views.
 */
@PageTitle("Home")
@Route(value = "")
@AnonymousAllowed
public class IntroView extends VerticalLayout implements BeforeEnterObserver{



    public IntroView() {

        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());

        setSpacing(false);

        VerticalLayout verticalLayout = new VerticalLayout();
//        verticalLayout.getThemeList().add("dark");

        Image img = new Image("images/seaqns-icon.png", "placeholder plant");
        img.setWidth("200px");
        verticalLayout.add(img);




        verticalLayout.add(new H2("Register for FREE, today"));
        verticalLayout.add(new Paragraph("Simple bookkeeping and payment solutions for small business owners 🤗"));


        EmailAddress emailField = new EmailAddress();
        emailField.setWidthFull();
        PasswordField passwordField = new PasswordField();
        passwordField.setWidthFull();
        passwordField.setLabel("Password");
        passwordField.setPlaceholder("Password");
        passwordField.setHelperText("A password must be at least 8 characters. It has to have at least one letter and one digit.");
        passwordField.setPattern("^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$");
        passwordField.setErrorMessage("Not a valid password");
        verticalLayout.add(new FormLayout(emailField,passwordField));

        Button registerBtn = new Button("Register");
        registerBtn.setWidthFull();
        registerBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        registerBtn.addClickShortcut(Key.ENTER);
        verticalLayout.add(registerBtn);

        setSizeFull();
//        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        verticalLayout. add(new Hr());

        ResetPasswordDialog button = new ResetPasswordDialog(new MyDialog());
        button.setText("Forgot password?");
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        Button go_to_login = new Button("Login");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        go_to_login.addClickListener(f -> UI.getCurrent().navigate(LoginView.class));

        verticalLayout.setMaxWidth("350px");
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        verticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        verticalLayout.getStyle().set("text-align", "center");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        headerLayout.add(button,go_to_login);
        add(headerLayout,verticalLayout);

        registerBtn.addClickListener(f -> {
            if(emailField.isInvalid()){
                return;
            }
            if(passwordField.isInvalid()){
                return;
            }

            UserService userService = ContextProvider.getBean(UserService.class);
            UserVO vo = new UserVO(emailField.getValue(), passwordField.getValue());
            ThisUser thisUser = userService.create(vo);
            if(thisUser != null){
                Authentication authenticate = new CustomAuthenticationProvider().authenticate(new UsernamePasswordAuthenticationToken(emailField.getValue(), passwordField.getValue()));
                if(!authenticate.isAuthenticated()){
                    Notification.show("Not Authenticated");
                } else {
                    UI.getCurrent().navigate(PersonView.class);
                }
            }
        });
    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Map<String, List<String>> parameters = event.getLocation().getQueryParameters().getParameters();
        boolean isVerif = parameters.containsKey("verif");
        if (isVerif) {
            String verify = parameters.get("verif").get(0);
            SpringVaadinSession.getCurrent().setAttribute("Verify",verify);
            event.forwardTo(LoginView.class);
        }
    }
}
