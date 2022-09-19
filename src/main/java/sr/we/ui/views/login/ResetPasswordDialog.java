package sr.we.ui.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.progressbar.ProgressBar;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.UserService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.ui.components.EmailAddress;
import sr.we.ui.components.MyDialog;

import java.util.List;

public class ResetPasswordDialog extends Button {

    private List<Business> businesses;

    public ResetPasswordDialog(MyDialog dialog) {
        super();
        dialog.setModal(true);
        dialog.setDraggable(false);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.setHeaderTitle("Reset your password");
        dialog.getElement().getStyle().set("position", "absolute");
        dialog.getElement().getStyle().set("top", "0px");
        dialog.getElement().getStyle().set("left", "0px");


        EmailAddress emailField = new EmailAddress();
        emailField.setWidth("500px");
        emailField.setHelperText("Enter user's registered email");


        dialog.add(emailField);


        dialog.add(new Hr());
        ProgressBar progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        dialog.add(progressBar);
        Button sendButton = new Button("Send email", e -> {

            String value = emailField.getValue();
            if(StringUtils.isBlank(value)){
                emailField.setInvalid(true);
                return;
            }
            emailField.setInvalid(false);
            e.getSource().setEnabled(false);
            progressBar.setVisible(true);
            String token = AuthenticatedUser.token();
            UI current = UI.getCurrent();
            new Thread(() -> {
                UserService bean = ContextProvider.getBean(UserService.class);
                bean.publishReset(emailField.getValue());

                current.access(()->{
                    Notification notification = new Notification();
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setText("You will receive an email with further instructions");
                    notification.setDuration(6000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.open();
                });
            }).start();
            new Thread(() -> {
                try {
                    Thread.sleep(59000);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }

                current.access(() -> {
                    e.getSource().setText("Resend email");
                    e.getSource().setEnabled(true);
                    progressBar.setVisible(false);
                });
            }).start();

        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(sendButton,cancelButton);
        boolean clear = false;
        addClickListener(e -> dialog.open());
    }


}
