package sr.we;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.client.HttpStatusCodeException;
import sr.we.shekelflowcore.exception.SecurityException;
import sr.we.shekelflowcore.exception.*;
import sr.we.ui.views.login.LoginView;

public class CustomNotificationHandler {


    public static void notify_(Throwable throwable) {
        if (UI.getCurrent() != null) {
            UI.getCurrent().access(() -> {
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.TOP_STRETCH);
                notification.setDuration(5000);


                // errors
                if (throwable instanceof AuthenticationServiceException exception) {
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Invalid Login Credentials");
                } else if (throwable instanceof AuthenticationException exception) {
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Could not Authenticate");
                } else if (throwable instanceof FrameworkException exception) {
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Framework Issue Detected, Try Again after Maintenance");
                } else if (throwable instanceof SecurityException exception) {
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Please login again for security reasons");
                    UI.getCurrent().navigate(LoginView.class);
                } else if (throwable instanceof ValidationException exception) {
                    notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    notification.setText(exception.getError().getMessage());
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.MIDDLE);
                } else if (throwable instanceof HttpStatusCodeException exception) {
                    notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    notification.setText("Service Issue Detected, Try Again after Maintenance");
                }

                // success or details
                else if (throwable instanceof SuccessThrowable exception) {
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setText(exception.getMessage());
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.TOP_STRETCH);
                } else if (throwable instanceof PrimaryThrowable exception) {
                    notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                    notification.setText(exception.getMessage());
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.MIDDLE);
                }


                // if nothing matches
                else {
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Something wrong happened, Try Again after Maintenance");
                }
                notification.open();
            });
        }
    }

}
