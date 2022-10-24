package sr.we.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotYetClick<T extends Component> implements ComponentEventListener<ClickEvent<T>> {
    @Override
    public void onComponentEvent(ClickEvent<T> event) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setText("Not yet implemented");
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }
}
