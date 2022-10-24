package sr.we;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpStatusCodeException;
import sr.we.shekelflowcore.exception.FrameworkException;
import sr.we.shekelflowcore.exception.SecurityException;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.ui.views.login.LoginView;

public class CustomErrorHandler implements ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);

    @Override
    public void error(ErrorEvent errorEvent) {
        logger.error("Something wrong happened", errorEvent.getThrowable());
        if(UI.getCurrent() != null) {
            UI.getCurrent().access(() -> {
                Notification notification = new Notification();
                notification.setPosition(Notification.Position.TOP_STRETCH);
                notification.setDuration(5000);


                if(errorEvent.getThrowable() instanceof AuthenticationException exception){
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Could not Authenticate");
                } else if(errorEvent.getThrowable() instanceof FrameworkException exception){
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Framework Issue Detected, Try Again after Maintenance");
                }else if(errorEvent.getThrowable() instanceof SecurityException exception){
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Please login again for security reasons");
                    UI.getCurrent().navigate(LoginView.class);
                }else if(errorEvent.getThrowable() instanceof ValidationException exception){
                    notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    notification.setText(exception.getError().getMessage());
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.MIDDLE);
                } else if(errorEvent.getThrowable() instanceof HttpStatusCodeException exception){
                    notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    notification.setText("Service Issue Detected, Try Again after Maintenance");
                } else {
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setText("Something wrong happened, Try Again after Maintenance");
                }

                notification.open();
            });
        }
    }
}