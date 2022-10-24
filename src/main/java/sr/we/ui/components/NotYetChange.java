package sr.we.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotYetChange<T> implements HasValue.ValueChangeListener<HasValue.ValueChangeEvent<T>> {
    @Override
    public void valueChanged(HasValue.ValueChangeEvent<T> event) {
                Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setText("Not yet implemented");
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }

}
