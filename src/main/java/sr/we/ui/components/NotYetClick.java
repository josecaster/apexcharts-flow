package sr.we.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import sr.we.CustomNotificationHandler;
import sr.we.shekelflowcore.exception.PrimaryThrowable;

public class NotYetClick<T extends Component> implements ComponentEventListener<ClickEvent<T>> {
    @Override
    public void onComponentEvent(ClickEvent<T> event) {
        CustomNotificationHandler.notify_(new PrimaryThrowable("Not yet implemented"));
    }
}
