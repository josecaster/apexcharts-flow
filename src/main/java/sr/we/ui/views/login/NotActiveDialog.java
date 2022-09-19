package sr.we.ui.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.UserService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.exception.SecurityException;
import sr.we.ui.components.MyDialog;

import java.util.Optional;

public class NotActiveDialog extends MyDialog {

    public NotActiveDialog() {
        VerticalLayout verticalLayout = new VerticalLayout();
        add(verticalLayout);
        verticalLayout.setSpacing(false);
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setMaxWidth("750px");


        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        verticalLayout.add(img);

        String verify = (String) SpringVaadinSession.getCurrent().getAttribute("Verify");

        initIt(verticalLayout, verify);

        verticalLayout.setSizeFull();
        verticalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        verticalLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        verticalLayout.getStyle().set("text-align", "center");
    }

    private void initIt(VerticalLayout verticalLayout, String verify) {
        verticalLayout.removeAll();
        if (verify == null || verify.isEmpty()) {

            verticalLayout.add(new H2("Your Account Email Is Not Yet Verified"));
            verticalLayout.add(new Paragraph("Please go to email we sent you and verify that this is your email"));
            Button resend_verification_email = new Button("Resend verification email");
            ProgressBar progressBar = new ProgressBar();
            progressBar.setVisible(false);
            progressBar.setIndeterminate(true);
            verticalLayout.add(resend_verification_email, progressBar);

            UI current = UI.getCurrent();

            resend_verification_email.addClickListener(f -> {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        current.access(() -> {
                            resend_verification_email.setEnabled(false);
                            progressBar.setVisible(true);
                        });
                    }
                }).start();
                UserService userService = ContextProvider.getBean(UserService.class);
                String token = AuthenticatedUser.token();
                userService.publishVerify(token);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(59000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        current.access(() -> {
                            resend_verification_email.setEnabled(true);
                            progressBar.setVisible(false);
                        });
                    }
                }).start();

            });

        } else {
            verticalLayout.add(new H2("Almost there"));
            verticalLayout.add(new Paragraph("By verifying you will secure your account and you will have extra security advantages"));
            UI current = UI.getCurrent();
            Button click_here = new Button("Click Here: " + verify);
            click_here.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            verticalLayout.add(click_here);
            String token = AuthenticatedUser.token();
            click_here.addClickListener(f -> {
                SpringVaadinSession.getCurrent().setAttribute("Verify", null);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        current.access(() -> {
                           initIt(verticalLayout, null);
                        });
                    }
                }).start();

                UserService userService = ContextProvider.getBean(UserService.class);
                ThisUser verify1 = userService.verify(token, verify);
                AuthenticatedUser authenticatedUser = ContextProvider.getBean(AuthenticatedUser.class);
                Optional<ThisUser> thisUser = authenticatedUser.get();
                if (thisUser.isPresent()) {
                    ThisUser thisUser1 = thisUser.get();
                    thisUser1.setActive(verify1.getActive());
                }
                UI.getCurrent().getPage().reload();
                if (!verify1.getActive()) {
                    throw new SecurityException("Failed to verify account");
                }
            });
        }
    }

}
