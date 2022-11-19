package sr.we.ui.components;

import com.vaadin.flow.component.HasValue;
import sr.we.CustomNotificationHandler;
import sr.we.shekelflowcore.exception.PrimaryThrowable;

public class NotYetChange<T> implements HasValue.ValueChangeListener<HasValue.ValueChangeEvent<T>> {
    @Override
    public void valueChanged(HasValue.ValueChangeEvent<T> event) {
        CustomNotificationHandler.notify_(new PrimaryThrowable("Not yet implemented"));
    }

}
